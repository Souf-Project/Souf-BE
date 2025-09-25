package com.souf.soufwebsite.domain.application.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
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

        @Schema(description="지원 가격 (FIXED일 경우 공고 가격, OFFER일 경우 내가 제안한 견적 가격)", example="400000")
        String appliedPrice,

        @Schema(description="견적 사유 (OFFER일 경우 내가 작성한 사유)", example="추가 리서치 필요성에 따른 가격 제안")
        String priceReason,

        @Schema(description = "지원일시", example = "2025-03-23")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        LocalDateTime appliedAt
) {
}
