package com.souf.soufwebsite.domain.member.dto.ReqDto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SigninReqDto(
        @Schema(description = "이메일", example = "user@example.com")
        @NotEmpty @Email String email,

        @Schema(description = "비밀번호", example = "P@ssw0rd1")
        @NotEmpty String password
){

}