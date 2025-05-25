package com.souf.soufwebsite.domain.member.dto.ReqDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SignupReqDto(

        @Schema(description = "사용자 살명", example = "홍길동")
        @NotEmpty String username,

        @Schema(description = "닉네임", example = "김개똥")
        @NotEmpty String nickname,

        @Schema(description = "이메일", example = "user@example.com")
        @NotEmpty @Email String email,

        @Schema(description = "비밀번호", example = "Passw0rd!")
        @NotEmpty String password,

        @Schema(description = "비밀번호 확인", example = "Passw0rd!")
        @NotEmpty String passwordCheck
) {
}