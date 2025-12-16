package com.souf.soufwebsite.domain.notification.scheduler;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import com.souf.soufwebsite.domain.notification.service.NotificationFacade;
import com.souf.soufwebsite.global.common.category.entity.FirstCategory;
import com.souf.soufwebsite.global.common.category.repository.FirstCategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecruitPublishAggregationScheduler {

    private final RedisTemplate<String, Object> redisTemplate;
    private final MemberRepository memberRepository;
    private final FirstCategoryRepository firstCategoryRepository;
    private final NotificationFacade notificationFacade;

    /**
     * 매 시간 정각마다 실행
     * Redis에 저장된 notif:agg:{memberId}:{firstId}:{secondId} 데이터를 읽어
     * 알림을 발행(SSE + 메일)
     */
    @Scheduled(cron = "0 0 * * * *")
    public void publishAggregatedRecruitNotifications() {
        Set<String> keys = redisTemplate.keys("notif:agg:*");
        if (keys == null || keys.isEmpty()) return;

        log.info("[Scheduler] {}개의 알림 집계 키 처리 시작", keys.size());

        for (String key : keys) {
            try {
                Object countObj = redisTemplate.opsForHash().get(key, "count");
                if (countObj == null) continue;

                long count = Long.parseLong(countObj.toString());

                // key 형식: notif:agg:{memberId}:{firstId}:{secondId}
                String[] parts = key.split(":");
                if (parts.length != 4) continue;

                Long memberId = Long.parseLong(parts[2]);
                Long firstId = Long.parseLong(parts[3]);

                Member member = memberRepository.findById(memberId).orElse(null);
                if (member == null || member.getEmail() == null) continue;

                FirstCategory first = firstCategoryRepository.findById(firstId).orElse(null);

                String categoryName = (first != null ? first.getName() : "");

                String body = String.format("%s 카테고리에 새로운 공고가 %d건 올라왔어요.", categoryName, count);

                notificationFacade.notify(
                        member,
                        NotificationType.RECRUIT_PUBLISHED,
                        "관심 카테고리 새 공고 알림",
                        body,
                        "RECRUIT",
                        null
                );

                log.info("[Scheduler] 알림 발행 완료 → {}", key);

                redisTemplate.delete(key);

            } catch (Exception e) {
                log.error("[Scheduler] 키 {} 처리 중 오류", key, e);
            }
        }
    }
}