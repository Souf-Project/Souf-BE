package com.souf.soufwebsite.domain.recruit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RecruitReqDto(
        @NotNull
        @Size(min = 2)
        String title,
        @NotNull
        @Size(max = 300)
        String content,
        @NotNull
        @Size(max = 30)
        String region,

        String deadLine
) {
}
