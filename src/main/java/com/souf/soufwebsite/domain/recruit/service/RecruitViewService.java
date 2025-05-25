package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class RecruitViewService {

    private final RecruitRepository recruitRepository;
    private final RedisUtil redisUtil;

    @Scheduled(cron = "0 0 0 ? * MON")
    public void syncViewCountsToDB() {
        Set<String> keys = redisUtil.getKeys("recruit:view:*");

        if (keys == null || keys.isEmpty()) return;

        for (String key : keys) {
            Long recruitId = Long.parseLong(key.replace("recruit:view:", ""));
            Long viewCountInRedis = redisUtil.get(key);
            if (viewCountInRedis == null || viewCountInRedis == 0L) continue;

            // DB의 기존 viewCount에 더해줌
            recruitRepository.increaseViewCount(recruitId, viewCountInRedis);

            // Redis 값 0으로 초기화
            redisUtil.set(key);
        }
    }
}
