package com.souf.soufwebsite.domain.recruit.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record RecruitSimpleResDto(

        Long recruitId,
        String title,
        List<Long> secondCategory,
        String content,
        String payment,
        String region,
        LocalDateTime deadLine,
        Long recruitCount,
        LocalDateTime lastModified
) {
    public static RecruitSimpleResDto of(Long recruitId, String title, Long secondCategoryId, String content,
                                         String payment, String region, LocalDateTime deadLine,
                                         Long recruitCount, LocalDateTime lastModified) {
        return new RecruitSimpleResDto(
                recruitId, title,
                new ArrayList<>(List.of(secondCategoryId)), // 초기 리스트
                content, payment, region, deadLine, recruitCount, lastModified
        );
    }

    public void addSecondCategory(Long id) {
        if (!this.secondCategory.contains(id)) {
            this.secondCategory.add(id);
        }
    }
}
