package com.souf.soufwebsite.domain.recruit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record RecruitReqDto(
        @NotEmpty(message = "공고문 제목은 필수입니다.")
        @Size(min = 2)
        String title,
        @NotNull(message = "공고문 내용은 필수입니다.")
        @Size(max = 300)
        String content,
        @NotNull(message = "진행 지역은 필수입니다.")
        @Size(max = 30)
        String region,

        @Future
        @NotNull(message = "마감 기한은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime deadline,

        @NotEmpty(message = "제시 금액은 필수입니다.")
        String payment,

        @NotNull(message = "우대사항은 옵션입니다.")
        @Size(max = 300)
        String preferentialTreatment,

        @NotNull(message = "적어도 한 개의 카테고리가 들어있어야 합니다.")
        List<CategoryDto> categoryDtos,

        List<String> originalFileNames
) {
}
