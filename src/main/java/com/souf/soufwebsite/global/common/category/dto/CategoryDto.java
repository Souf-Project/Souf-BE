package com.souf.soufwebsite.global.common.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryDto(
        @Schema(description = "1차 카테고리 ID", example = "10")
        Long firstCategory,
        @Schema(description = "2차 카테고리 ID", example = "23")
        Long secondCategory,
        @Schema(description = "3차 카테고리 ID", example = "57")
        Long thirdCategory) {

}