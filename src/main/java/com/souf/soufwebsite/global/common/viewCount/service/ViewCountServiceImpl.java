package com.souf.soufwebsite.global.common.viewCount.service;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.viewCount.dto.ViewCountResDto;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountServiceImpl implements ViewCountService {

    private static final String TOTAL_COUNT = "totalCount";

    private final RedisUtil redisUtil;
    private final MemberRepository memberRepository;
    private final RecruitRepository recruitRepository;

    @Override
    @Cacheable(value = "viewCountToMain",
                key = "'main:count'")
    public ViewCountResDto getViewCountFromMain(){
        log.info("캐싱 실패하여 데이터를 조회합니다.");
        String totalCount = getViewCountKey();

        Long studentCount = memberRepository.countAllByRole(RoleType.STUDENT);
        Long recruitCount = recruitRepository.count();

        return new ViewCountResDto(redisUtil.get(totalCount), studentCount, recruitCount);
    }

    @CachePut(value = "viewCountToMain",
            key = "'main:count'")
    public ViewCountResDto refreshViewCountCache() {
        log.info("스케줄러에 의해 캐시 강제 갱신");
        String totalCountKey = getViewCountKey();
        redisUtil.setIfAbsent(totalCountKey);
        Long totalCount = redisUtil.get(totalCountKey);
        Long studentCount = memberRepository.countAllByRole(RoleType.STUDENT);
        Long recruitCount = recruitRepository.count();
        return new ViewCountResDto(totalCount, studentCount, recruitCount);
    }

    @Override
    public void initiateRedisKey() {
        redisUtil.set(getViewCountKey());
    }

    private String getViewCountKey(){
        return "view:main:" + TOTAL_COUNT;
    }
}
