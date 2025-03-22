package com.souf.soufwebsite.domain.user.dto.ReqDto;

import jakarta.validation.constraints.NotNull;

public record ResetReqDto(
        @NotNull String email,
        @NotNull String newPassword,
        @NotNull String confirmPassword
) {
}
