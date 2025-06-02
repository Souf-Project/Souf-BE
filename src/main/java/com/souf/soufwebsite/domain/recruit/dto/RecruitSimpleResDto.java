package com.souf.soufwebsite.domain.recruit.dto;

import java.time.LocalDateTime;

public record RecruitSimpleResDto(

        Long recruitId,
        String title,
        Long secondCategory,
        String content,
        String payment,
        String region,
        LocalDateTime deadLine,
        Long recruitCount,
        LocalDateTime lastModified
) {
}
