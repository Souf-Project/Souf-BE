package com.souf.soufwebsite.domain.application.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ApplicationOfferReqDto(
        @Schema(description = "지원자가 제안한 가격 (OFFER(견적받기)일 떄 필수)", example = "450000")
        String offeredPrice,

        @Schema(description = "가격 제안 사유 (OFFER(견적받기)일 때 필수)", example = "리서치/시안 2안 포함")
        String priceReason
) {
}
