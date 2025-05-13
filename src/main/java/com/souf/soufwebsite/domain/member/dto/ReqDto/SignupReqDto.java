package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignupReqDto(
        @NotNull @Email String email,
        @NotNull String password,
        @NotNull String username,
        @NotNull String nickname
) {
}