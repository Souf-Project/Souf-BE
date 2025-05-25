package com.souf.soufwebsite.domain.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ApplicationReqDto(
        @Schema(description = "공고 ID", example = "123")
        @NotNull(message = "recruitId is required")
        Long recruitId
) {}