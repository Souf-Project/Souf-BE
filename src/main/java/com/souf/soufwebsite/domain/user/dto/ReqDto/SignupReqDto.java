package com.souf.soufwebsite.domain.user.dto.ReqDto;

import jakarta.validation.constraints.NotNull;

public record SignupReqDto(
        @NotNull String email,
        @NotNull String password,
        @NotNull String username,
        @NotNull String nickname
) {
}
