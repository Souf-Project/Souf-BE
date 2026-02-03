package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.res.FeedSimpleResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedScheduledService {

    private final FeedRepository feedRepository;
    private final FeedConverter feedConverter;
    private final FeedCacheService feedCacheService;
    private final CacheManager cacheManager;
    private final StringRedisTemplate redisTemplate;

    public static final String WEEKLY_ZSET = "feed:views:weekly:";
    public static final String TOTAL_HASH = "feed:views:total:";

    @Transactional
    public void syncWeeklyViewCountsToDB() {
        Set<ZSetOperations.TypedTuple<String>> tuples = redisTemplate.opsForZSet().rangeWithScores(WEEKLY_ZSET, 0, -1);
        if(tuples == null || tuples.isEmpty()) {
            return;
        }

        feedRepository.updateWeeklyViews();
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            String value = tuple.getValue();
            Double score = tuple.getScore();

            if(value == null || score == null) {
                continue;
            }

            Long feedId = Long.valueOf(value);
            long weeklyViewCount = score.longValue();

            feedRepository.findById(feedId).ifPresent(feed -> feedRepository.increaseWeeklyViewCount(feedId, weeklyViewCount));
        }

        redisTemplate.delete(WEEKLY_ZSET); // 주간 초기화

        log.info("피드 주간 조회수 DB에 반영 완료");
    }

    @Transactional
    public void syncTotalViewCountsToDB() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(TOTAL_HASH);

        if(entries.isEmpty()) {
            return;
        }

        for(Object key : entries.keySet()) {
            Object value = entries.get(key);
            if(value == null) {
                continue;
            }

            Long feedId = Long.valueOf((String) key);
            Long totalViewCount = Long.valueOf((String) value);
            feedRepository.findById(feedId).ifPresent(feed -> feedRepository.increaseTotalViewCount(feedId, totalViewCount));
        }

        redisTemplate.delete(TOTAL_HASH);

        log.info("누적 조회수 DB에 반영 완료");
    }

//    private String buildKey(Pageable pageable) {
//        return "page:" + pageable.getPageNumber() + ":" + pageable.getPageSize();
//    }

    @SuppressWarnings("unchecked")
    public void patchOne(Long feedId) {
        Cache cache = cacheManager.getCache("popularFeeds");
        if (cache == null) return;

        Cache.ValueWrapper wrapper = cache.get("feed:popular");
        if (wrapper == null) { feedCacheService.refreshPopularFeeds(); return; }

        List<FeedSimpleResDto> cached = (List<FeedSimpleResDto>) wrapper.get();
        if (cached == null || cached.isEmpty()) { feedCacheService.refreshPopularFeeds(); return; }

        Feed updated = feedRepository.findById(feedId).orElse(null);
        if (updated == null) { feedCacheService.refreshPopularFeeds(); return; }

        FeedSimpleResDto newDto = feedConverter.getFeedSimpleResDto(updated);

        List<FeedSimpleResDto> patched = cached.stream()
                .map(d -> d.feedId().equals(feedId) ? newDto : d)
                .toList();

        cache.put("feed:popular", patched);
    }

    @SuppressWarnings("unchecked")
    public boolean cacheContains(Long feedId) {
        Cache cache = cacheManager.getCache("popularFeeds");
        if (cache == null) return false;

        Cache.ValueWrapper w = cache.get("feed:popular");
        if (w == null || w.get() == null) return false;

        List<FeedSimpleResDto> list = (List<FeedSimpleResDto>) w.get();
        return list != null && list.stream().anyMatch(d -> d.feedId().equals(feedId));
    }
}
