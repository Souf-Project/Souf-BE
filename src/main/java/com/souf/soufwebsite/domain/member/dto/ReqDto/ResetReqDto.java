package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record ResetReqDto(
        @NotEmpty @Email String email,
        @NotEmpty String newPassword,
        @NotEmpty String confirmPassword
) {
}