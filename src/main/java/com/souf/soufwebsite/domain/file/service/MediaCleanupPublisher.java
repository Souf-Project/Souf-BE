package com.souf.soufwebsite.domain.file.service;

import com.souf.soufwebsite.domain.file.event.MediaCleanupEvent;
import com.souf.soufwebsite.domain.file.event.MediaCleanupUrlsEvent;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MediaCleanupPublisher {
    private final ApplicationEventPublisher publisher;

    public void publish(PostType type, Long postId) {
        publisher.publishEvent(new MediaCleanupEvent(type, postId));
    }

    public void publishUrls(PostType postType, Long postId, List<String> urls) {
        if (urls != null && !urls.isEmpty()) {
            publisher.publishEvent(new MediaCleanupUrlsEvent(postType, postId, urls));
        }
    }
}
