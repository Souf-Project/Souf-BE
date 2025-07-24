package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedSimpleResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedScheduledService {

    private final FeedRepository feedRepository;
    private final RedisUtil redisUtil;
    private final CacheManager cacheManager;
    private final FeedConverter feedConverter;

    @Transactional
    public void syncViewCountsToDB() {
        Set<String> keys = redisUtil.getKeys("feed:view:*");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long feedId = Long.parseLong(key.replace("feed:view:", ""));
            Long viewCountInRedis = redisUtil.get(key);
            if (viewCountInRedis == null || viewCountInRedis == 0L) continue;

            // DB의 기존 viewCount에 더해줌
            feedRepository.increaseViewCount(feedId, viewCountInRedis);

            // Redis 값 0으로 초기화
            redisUtil.set(key);
        }
        log.info("피드 조회수 스케줄링 작업 완료");
    }

    @Transactional
    public void refreshPopularFeeds() {
        Cache cache = cacheManager.getCache("popularFeeds");
        if (cache == null) {
            log.error("popularFeeds 캐시가 설정되지 않았습니다.");
            return;
        }

        for (int i = 0; i < 3; i++) {
            Pageable pageable = PageRequest.of(i, 6);
            // repo가 null을 주지 않는 게 보통이지만 혹시 모르니 방어코드
            Page<Feed> feeds = feedRepository.findByOrderByViewCountDesc(pageable);
            if (feeds == null) feeds = Page.empty(pageable);

            List<FeedSimpleResDto> dto = feeds.getContent().stream()
                    .map(feedConverter::getFeedSimpleResDto)
                    .toList();

            cache.put(buildKey(pageable), dto);
        }
        log.info("인기 피드 캐싱 완료");
    }

    private String buildKey(Pageable pageable) {
        return "page:" + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
