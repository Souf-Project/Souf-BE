package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedScheduledService {

    private final FeedRepository feedRepository;
    private final RedisUtil redisUtil;
    private final CacheManager cacheManager;
    private final FeedConverter feedConverter;

    @Scheduled(cron = "0 0 0 * * *")
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
    }

    @Scheduled(cron = "0 2 0 * * *")
    public void refreshPopularFeeds() {

        for(int i=0;i<3;i++) {
            Pageable pageable = PageRequest.of(i, 6);
            Page<Feed> feeds = feedRepository.findByOrderByViewCountDesc(pageable);
            cacheManager.getCache("popularFeeds").put("page:" + i, feeds.map(feedConverter::getFeedSimpleResDto));
        }
    }
}
