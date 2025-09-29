package com.souf.soufwebsite.global.common.viewCount.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.viewCount.dto.ViewCountResDto;
import com.souf.soufwebsite.global.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountServiceImpl implements ViewCountService {

    private static final String TOTAL_COUNT = "totalCount";
    public static final String TOTAL_HASH = "views:total:";
    public static final String FEED_WEEKLY_ZSET = "feed:views:weekly:";

    private final StringRedisTemplate stringRedisTemplate;
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

    @Override
    public long updateTotalViewCount(Member member, PostType type, Long postId, Long currentViewCount, String ip, String userAgent) {

        String userKey;
        if(member != null){
            userKey = "member:" + member.getId();
        } else {
            userKey = "guest:" + ip + ":" + userAgent.hashCode();
        }

        String key = getTotalHash(type);

        String redisKey = "view:" + postId + ":" + userKey;
        if(type.equals(PostType.REVIEW)){
            redisKey = "review:" + redisKey;
        } else if (type.equals(PostType.FEED)) {
            redisKey = "feed:" + redisKey;
        } else if (type.equals(PostType.RECRUIT)) {
            redisKey = "recruit:" + redisKey;
        }

        Boolean isNew = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, "1", Duration.ofMinutes(10));

        if (Boolean.TRUE.equals(isNew)){
            if(type.equals(PostType.FEED))
                stringRedisTemplate.opsForZSet().incrementScore(FEED_WEEKLY_ZSET, String.valueOf(postId), 1D); // 주간 조회수 카운트
            return stringRedisTemplate.opsForHash().increment(key, String.valueOf(postId), 1L)
                    + currentViewCount;
        }

        log.info("{} 조회수 중복 방지", type.name());
        Object viewCount = stringRedisTemplate.opsForHash().get(key, String.valueOf(postId));
        long redisReviewCount = 0L;
        if(viewCount != null){
            redisReviewCount = Long.parseLong(viewCount.toString());
        }

        return redisReviewCount + currentViewCount;
    }

    @Override
    public void deleteViewCountFromRedis(PostType type, Long postId) {

        if(type.equals(PostType.FEED))
            stringRedisTemplate.opsForZSet().remove(FEED_WEEKLY_ZSET, String.valueOf(postId));

        stringRedisTemplate.opsForHash().delete(getTotalHash(type), String.valueOf(postId));
    }

    private String getTotalHash(PostType type) {
        String key = TOTAL_HASH;
        if(type.equals(PostType.REVIEW)){
            key = "review:" + TOTAL_HASH;
        } else if (type.equals(PostType.FEED)) {
            key = "feed:" + TOTAL_HASH;
        } else if (type.equals(PostType.RECRUIT)) {
            key = "recruit:" + TOTAL_HASH;
        }

        return key;
    }

    private String getViewCountKey(){
        return "view:main:" + TOTAL_COUNT;
    }
}
