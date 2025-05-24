package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.domain.recruit.entity.Recruit;

public record RecruitPopularityResDto(
        String title,
        String content,
        Long secondCategory
) {
    public static RecruitPopularityResDto of(Recruit recruit) {
        return new RecruitPopularityResDto(
                recruit.getTitle(),
                recruit.getContent(),
                recruit.getCategories().get(0).getSecondCategory().getId()
        );
    }
}
