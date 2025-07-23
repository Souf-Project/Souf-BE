package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.recruit.dto.RecruitPopularityResDto;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
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
public class RecruitScheduledService {

    private final RecruitRepository recruitRepository;
    private final RedisUtil redisUtil;
    private final CacheManager cacheManager;

    @Transactional
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
        log.info("공고문 조회수 스케줄링 작업 완료");
    }

    @Transactional
    public void updateRecruitableStatus() {
        List<Recruit> recruitList = recruitRepository.findByRecruitableTrue();

        for (Recruit recruit : recruitList) {
            recruit.checkAndUpdateRecruitable();
        }
        log.info("공고문 마감 상태 스케줄링 작업 완료");
    }

    @Transactional
    public void refreshPopularRecruits() {
        Cache cache = cacheManager.getCache("popularRecruits");
        if (cache == null) {
            log.error("popularRecruits 캐시가 설정되지 않았습니다.");
            return;
        }

        Pageable pageable = PageRequest.of(0, 10);

        Page<Recruit> recruits = recruitRepository.findByRecruitableTrueOrderByViewCountDesc(pageable);

        List<RecruitPopularityResDto> dto = recruits.getContent().stream()
                .map(RecruitPopularityResDto::of).toList();

        cache.put(buildKey(pageable), dto);
    }

    private String buildKey(Pageable pageable) {
        return "page:" + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
