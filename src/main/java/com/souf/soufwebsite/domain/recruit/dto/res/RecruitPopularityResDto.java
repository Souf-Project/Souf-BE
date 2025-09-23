package com.souf.soufwebsite.domain.recruit.dto.res;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record RecruitPopularityResDto(
        Long recruitId,
        String title,
        String content,
        Long firstCategory,
        Long secondCategory,
        Long deadLine,
        String price,
        String nickname,
        String profile
) {
    public static RecruitPopularityResDto of(Recruit recruit, String profile) {
        return new RecruitPopularityResDto(
                recruit.getId(),
                recruit.getTitle(),
                recruit.getContent(),
                recruit.getCategories().get(0).getFirstCategory().getId(),
                recruit.getCategories().get(0).getSecondCategory().getId(),
                leftDays(recruit.getDeadline()),
                recruit.getPrice(),
                recruit.getMember().getNickname(),
                profile
        );
    }

    private static long leftDays(LocalDateTime deadline) {
        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.DAYS.between(deadline, now);
    }
}
