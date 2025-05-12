package com.souf.soufwebsite.domain.member.dto.ReqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SignupReqDto(
        @NotNull @Size(min = 5, max = 30) @Email String email,
        @NotNull @Size(min = 8, max = 20) String password,
        @NotNull @Size(min = 2, max = 20) String username,
        @NotNull @Size(min = 2, max = 20) String nickname
) {
}
