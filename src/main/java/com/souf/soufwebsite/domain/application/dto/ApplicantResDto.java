package com.souf.soufwebsite.domain.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ApplicantResDto(
        @Schema(description="지원서 ID", example="456")
        Long applicationId,

        @Schema(description="지원자 정보")
        MemberResDto member,

        @Schema(description="지원일시", example="2025-06-01")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
        LocalDateTime appliedAt,

        @Schema(description="진행 상태", example="PENDING")
        String status
) {
}
