package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.global.common.FirstCategory;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

        @Future
        LocalDateTime deadline,
        String payment,
        @Size(max = 300)
        String preferentialTreatment,
        FirstCategory firstCategory,

        List<String> originalFileNames
) {
}
