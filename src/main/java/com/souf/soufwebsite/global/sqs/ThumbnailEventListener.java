package com.souf.soufwebsite.global.sqs;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.repository.MediaRepository;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ThumbnailEventListener {

    private final MediaRepository mediaRepository;

    @SqsListener("souf-thumbnail-created-queue")
    public void handleThumbnailEvent(String message) {
        try {
            S3EventNotification notification = S3EventNotification.parseJson(message);
            for (S3EventNotification.S3EventNotificationRecord record : notification.getRecords()) {
                String s3Key = record.getS3().getObject().getKey();
                String fileName = Paths.get(s3Key).getFileName().toString(); // 183a-1asjk-...mp4.png

                if (!fileName.endsWith(".png")) {
                    log.warn("Unexpected file name: {}", fileName);
                    continue;
                }

                String originalFileName = fileName.replace(".png", ""); // 183a-1asjk-..mp4

                log.info("S3 Event Key: {}, fileName: {}", s3Key, fileName);

                Optional<Media> mediaOpt = mediaRepository.findFirstByOriginalUrlEndingWith(originalFileName);
                if (mediaOpt.isPresent()) {
                    Media media = mediaOpt.get();
                    media.addThumbnailUrl(s3Key);
                    mediaRepository.save(media);
                    log.info("썸네일 URL 저장 완료: {}", s3Key);
                } else {
                    log.warn("Media not found for original file: {}", originalFileName);
                }
            }
        } catch (Exception e) {
            log.error("Error Processing Thumbnail SQS message", e);
            throw e;
        }
    }
}
