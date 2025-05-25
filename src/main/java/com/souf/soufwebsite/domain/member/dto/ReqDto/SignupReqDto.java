package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record SignupReqDto(
        @NotEmpty String username,
        @NotEmpty String nickname,
        @NotEmpty @Email String email,
        @NotEmpty String password,
        @NotEmpty String passwordCheck
) {
}