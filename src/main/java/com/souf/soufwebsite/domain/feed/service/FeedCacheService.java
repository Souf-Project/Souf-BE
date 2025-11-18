package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedSimpleResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedCacheService {

    private final CacheManager cacheManager;
    private final FeedRepository feedRepository;
    private final FeedConverter feedConverter;

    @Transactional(readOnly = true)
    public void refreshPopularFeeds() {
        Cache cache = cacheManager.getCache("popularFeeds");
        if (cache == null) {
            log.error("popularFeeds 캐시가 설정되지 않았습니다.");
            return;
        }

        List<Feed> popularFeeds = feedRepository.findTop6ByOrderByWeeklyViewCountDesc();
        List<FeedSimpleResDto> result = popularFeeds.stream()
                .map(feedConverter::getFeedSimpleResDto)
                .toList();
        cache.put("feed:popular", result);

        log.info("인기 피드 캐싱 완료");
    }
}
