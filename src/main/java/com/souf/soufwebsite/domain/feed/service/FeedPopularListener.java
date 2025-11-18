package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.event.FeedChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedPopularListener {

    private final FeedScheduledService feedScheduledService;
    private final FeedCacheService feedCacheService;

    @EventListener
    public void onChanged(FeedChangedEvent e) {
        switch (e.type()) {
            case CONTENT_UPDATED -> {
                log.info("CONTENT_UPDATED : 피드 내용 수정 이벤트 감지: {}", e.id());
                if (feedScheduledService.cacheContains(e.id()))
                    feedScheduledService.patchOne(e.id());
            }

            case DELETED -> {
                log.info("DELETED : 피드 삭제 이벤트 감지: {}", e.id());
                if (feedScheduledService.cacheContains(e.id()))
                    feedCacheService.refreshPopularFeeds();
            }
        }
    }
}
