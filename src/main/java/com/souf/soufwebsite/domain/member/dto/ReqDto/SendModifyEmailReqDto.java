package com.souf.soufwebsite.domain.member.dto.ReqDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record SendModifyEmailReqDto(
        @Schema(description = "기존의 회원가입에 사용한 일반 이메일", example = "test@test.com")
        @NotEmpty @Email
        String originalEmail,
        @Schema(description = "학생 인증에 사용할 이메일", example = "test@test.ac.kr")
        @NotEmpty @Email
        String acKrEmail
) {
}
