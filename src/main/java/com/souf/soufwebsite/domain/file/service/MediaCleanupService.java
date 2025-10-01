package com.souf.soufwebsite.domain.file.service;

import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.event.MediaCleanupEvent;
import com.souf.soufwebsite.domain.file.repository.MediaRepository;
import com.souf.soufwebsite.global.util.S3KeyUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MediaCleanupService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final MediaRepository mediaRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MediaCleanupEvent e) {
        // 1) DB에서 해당 포스트의 미디어 가져오기
        List<Media> medias = mediaRepository.findAllByPostTypeAndPostId(e.postType(), e.postId());
        if (medias.isEmpty()) return;

        // 2) URL -> Key 변환(원본+썸네일 모두) → SDK v2의 ObjectIdentifier 리스트 생성
        List<ObjectIdentifier> objects = new ArrayList<>();
        for (Media m : medias) {
            addObject(objects, m.getOriginalUrl());
            addObject(objects, m.getThumbnailUrl()); // 썸네일 필드가 있으면 같이 제거
            // 파생 사이즈/경로가 더 있다면 여기서 추가
        }

        // 3) S3 일괄 삭제 (키가 하나라도 있으면)
        if (!objects.isEmpty()) {
            Delete delete = Delete.builder()
                    .objects(objects)
                    .build();

            DeleteObjectsRequest req = DeleteObjectsRequest.builder()
                    .bucket(bucket)
                    .delete(delete)
                    .build();

            try {
                s3Client.deleteObjects(req);
            } catch (S3Exception ex) { // ✅ v2 예외
                log.warn("S3 deleteObjects failed: {}", ex.awsErrorDetails().errorMessage(), ex);
                // 정책에 따라 DB 삭제 보류/진행 결정. 보통 DB는 기준으로 두고 진행.
            }
        }

        // 4) DB media 레코드 제거
        mediaRepository.deleteAll(medias);
    }

    private void addObject(List<ObjectIdentifier> objects, String url) {
        String key = S3KeyUtils.extractKeyFromUrl(url, bucket); // 정적 유틸 사용 (빈 주입 X)
        if (key != null && !key.isBlank()) {
            objects.add(ObjectIdentifier.builder().key(key).build());
        }
    }
}