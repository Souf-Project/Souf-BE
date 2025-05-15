package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record SignupReqDto(
        @NotEmpty @Email String email,
        @NotEmpty String password,
        @NotEmpty String username,
        @NotEmpty String nickname
) {
}