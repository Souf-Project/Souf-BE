package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ResetReqDto(
        @NotNull @Email String email,
        @NotNull String newPassword,
        @NotNull String confirmPassword
) {
}