package com.souf.soufwebsite.domain.recruit.service;

import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.recruit.dto.res.RecruitPopularityResDto;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitScheduledService {

    private final RecruitRepository recruitRepository;
    private final StringRedisTemplate redisTemplate;
    private final CacheManager cacheManager;
    private final FileService fileService;

    public static final String TOTAL_HASH = "recruit:views:total:";

    @Transactional
    public void syncViewCountsToDB() {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(TOTAL_HASH);

        if(entries.isEmpty()) {
            return;
        }

        for(Object key : entries.keySet()) {
            Object value = entries.get(key);
            if(value == null)
                continue;

            Long recruitId = Long.valueOf(String.valueOf(key));
            Long totalViewCount = Long.valueOf(String.valueOf(value));
            recruitRepository.findById(recruitId).ifPresent(recruit -> {
                recruitRepository.increaseViewCount(recruitId, totalViewCount);
            });
        }

        redisTemplate.delete(TOTAL_HASH);

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

        LocalDateTime now = LocalDateTime.now();
        List<Recruit> popularRecruits = recruitRepository.findTop5ByRecruitableAndDeadlineAfterOrderByDeadlineAsc(now);

        log.info("공고문 로직 실행 중");

        List<RecruitPopularityResDto> results = popularRecruits.stream().map(
                r -> {
                    String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, r.getMember().getId());
                    return RecruitPopularityResDto.of(r, mediaUrl);
                }
        ).toList();
        log.info("popular Recruit size: {}", results.size());

        cache.put("recruit:popular", results);
    }

    private String buildKey(Pageable pageable) {
        return "page:" + pageable.getPageNumber() + ":" + pageable.getPageSize();
    }
}
