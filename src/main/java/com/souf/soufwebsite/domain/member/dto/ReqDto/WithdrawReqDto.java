package com.souf.soufwebsite.domain.member.dto.ReqDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record WithdrawReqDto(
        @Schema(description = "탈퇴 전 비밀번호 확인", example = "1q2w3er4r1")
        @NotEmpty String password
) {
}
