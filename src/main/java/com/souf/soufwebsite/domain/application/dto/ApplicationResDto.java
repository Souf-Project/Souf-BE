package com.souf.soufwebsite.domain.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ApplicationResDto(
        @Schema(description = "지원 ID", example = "456")
        Long id,
        @Schema(description = "공고 ID", example = "123")
        Long recruitId,
        @Schema(description = "회원 ID", example = "1")
        Long memberId,
        @Schema(description = "지원일시", example = "2025-06-01T12:00:00")
        String appliedAt
) {}