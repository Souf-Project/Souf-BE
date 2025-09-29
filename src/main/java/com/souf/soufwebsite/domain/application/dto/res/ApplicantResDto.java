package com.souf.soufwebsite.domain.application.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ApplicantResDto(
        @Schema(description="지원서 ID", example="456")
        Long applicationId,

        @Schema(description="지원자 정보")
        MemberResDto member,

        @Schema(description="지원 가격 (FIXED일 경우 null, OFFER일 경우 지원자가 작성한 견적 가격)", example="500000")
        String priceOffer,

        @Schema(description="견적 사유 (FIXED일 경우 null, OFFER 정책일 경우 필수)", example="작업 범위 확대 요청으로 인한 견적")
        String priceReason,

        @Schema(description="지원일시", example="2025-06-01")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        LocalDateTime appliedAt,

        @Schema(description="진행 상태", example="PENDING")
        String status
) {
}
