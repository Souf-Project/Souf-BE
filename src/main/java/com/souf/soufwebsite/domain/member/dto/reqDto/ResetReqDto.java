package com.souf.soufwebsite.domain.member.dto.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ResetReqDto(
        @NotEmpty @Email String email,
        @NotEmpty String newPassword,
        @NotEmpty String confirmPassword
) {
}