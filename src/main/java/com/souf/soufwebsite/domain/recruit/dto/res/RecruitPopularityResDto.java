package com.souf.soufwebsite.domain.recruit.dto.res;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;

import java.time.LocalDateTime;

public record RecruitPopularityResDto(
        Long recruitId,
        String title,
        String content,
        Long firstCategory,
        Long secondCategory,
        LocalDateTime deadLine,
        String minPayment,
        String maxPayment,
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
                recruit.getDeadline(),
                recruit.getMinPayment(),
                recruit.getMaxPayment(),
                recruit.getMember().getNickname(),
                profile
        );
    }
}
