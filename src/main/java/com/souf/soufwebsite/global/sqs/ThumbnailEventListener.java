package com.souf.soufwebsite.global.sqs;

import com.amazonaws.services.s3.event.S3EventNotification;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.repository.MediaRepository;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThumbnailEventListener {

    private final MediaRepository mediaRepository;

    @SqsListener("souf-thumbnail-created-queue")
    public void handleThumbnailEvent(String message) {
        S3EventNotification notification = S3EventNotification.parseJson(message);
        for(S3EventNotification.S3EventNotificationRecord record : notification.getRecords()) {
            String s3Key = record.getS3().getObject().getKey();
            String urlType = s3Key.substring(s3Key.lastIndexOf("/") + 1, s3Key.lastIndexOf("."));

            Media media = mediaRepository.findByOriginalUrlContains(urlType);
            media.addThumbnailUrl(record.getS3().getObject().getKey());
        }
    }
}
