package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FeedViewService {

    private final FeedRepository feedRepository;
    private final RedisUtil redisUtil;

    @Scheduled(cron = "0 0 0 ? * MON")
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
}
