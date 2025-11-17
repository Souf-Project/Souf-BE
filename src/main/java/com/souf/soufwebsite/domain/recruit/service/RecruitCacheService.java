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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitCacheService {

    private final CacheManager cacheManager;
    private final RecruitRepository recruitRepository;
    private final FileService fileService;


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
}
