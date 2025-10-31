package com.souf.soufwebsite.domain.member.dto.reqDto;

import com.souf.soufwebsite.domain.member.service.VerificationPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VerifyEmailReqDto(
        @Schema(description = "인증번호를 전송한 이메일", example = "test@test.com")
        @NotBlank @Email
        String email,
        @Schema(description = "인증번호", example = "123456")
        @NotBlank
        String code,
        @Schema(description = "인증번호 확인 목적", example = "SIGNUP, RESET, MODIFY")
        @NotNull
        VerificationPurpose purpose
) {
}
