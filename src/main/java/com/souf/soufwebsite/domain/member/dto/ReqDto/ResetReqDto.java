package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetReqDto(
        @NotNull @Size(min = 5, max = 30) @Email String email,
        @NotNull @Size(min = 8, max = 20) String newPassword,
        @NotNull @Size(min = 8, max = 20) String confirmPassword
) {
}
