package com.souf.soufwebsite.domain.application.dto;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record MyApplicationResDto(
        @Schema(description = "공고 ID", example = "123")
        Long recruitId,

        @Schema(description = "공고문 제목", example = "디지털 광고 매체 그래픽 디자인")
        String title,

        @Schema(description = "기업명", example = "㈜스프주식회사")
        String nickname,

        @Schema(description = "카테고리 목록")
        List<CategoryDto> categoryDtos,

        @Schema(description = "지원 상태", example = "PENDING")
        String status,

        @Schema(description = "지원일시", example = "2025-03-23T10:15:30")
        LocalDateTime appliedAt
) {
}
