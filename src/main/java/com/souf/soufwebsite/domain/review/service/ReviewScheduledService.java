package com.souf.soufwebsite.domain.review.service;

import com.souf.soufwebsite.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewScheduledService {

    private final ReviewRepository reviewRepository;
    private final StringRedisTemplate redisTemplate;

    public static final String TOTAL_HASH = "review:views:total:";

    @Transactional
    public void syncViewCountsToDB(){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(TOTAL_HASH);

        if(entries.isEmpty()){
            return;
        }

        for(Object key : entries.keySet()){
            Object value = entries.get(key);
            if(value == null)
                continue;

            Long reviewId = Long.valueOf(String.valueOf(key));
            Long totalViewCount = Long.valueOf(String.valueOf(value));
            reviewRepository.findById(reviewId).ifPresent(review -> {
                reviewRepository.increaseViewCount(reviewId, totalViewCount);
            });
        }

        redisTemplate.delete(TOTAL_HASH);

        log.info("후기 조회수 스케줄링 작업 완료");
    }


}
