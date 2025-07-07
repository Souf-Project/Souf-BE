package com.souf.soufwebsite.domain.member.dto.ReqDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record SendEmailReqDto(
        @Schema(description = "인증번호를 전송할 이메일", example = "test@test.com")
        @NotEmpty @Email
        String email
) {
}
