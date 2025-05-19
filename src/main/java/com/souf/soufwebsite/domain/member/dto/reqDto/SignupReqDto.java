package com.souf.soufwebsite.domain.member.dto.reqDto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record SignupReqDto(
        @NotEmpty @Email String email,
        @NotEmpty String password,
        @NotEmpty String username,
        @NotEmpty String nickname
) {
}