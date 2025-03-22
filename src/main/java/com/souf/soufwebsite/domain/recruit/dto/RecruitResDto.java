package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.global.common.FirstCategory;

import java.time.LocalDateTime;

public record RecruitResDto(
        Long recruitId,
        String title,
        String content,
        FirstCategory firstCategory,
        String region,
        LocalDateTime deadline,
        String payment,
        String preferentialTreatment,
        String nickname
) {
    public static RecruitResDto from(Recruit recruit, String nickname) {
        return new RecruitResDto(recruit.getId(),
                recruit.getTitle(),
                recruit.getContent(),
                recruit.getFirstCategory(),
                recruit.getRegion(),
                recruit.getDeadline(),
                recruit.getPayment(),
                recruit.getPreferentialTreatment(),
                nickname);
    }
}
