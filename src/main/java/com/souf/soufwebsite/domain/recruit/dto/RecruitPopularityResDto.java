package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;

import java.time.LocalDateTime;

public record RecruitPopularityResDto(
        Long recruitId,
        String title,
        String content,
        Long firstCategory,
        Long secondCategory,
        LocalDateTime deadLine
) {
    public static RecruitPopularityResDto of(Recruit recruit) {
        return new RecruitPopularityResDto(
                recruit.getId(),
                recruit.getTitle(),
                recruit.getContent(),
                recruit.getCategories().get(0).getFirstCategory().getId(),
                recruit.getCategories().get(0).getSecondCategory().getId(),
                recruit.getDeadline()
        );
    }
}
