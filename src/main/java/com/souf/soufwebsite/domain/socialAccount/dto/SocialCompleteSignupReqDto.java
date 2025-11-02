package com.souf.soufwebsite.domain.socialAccount.dto;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.SignupReqDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record SocialCompleteSignupReqDto(
        @Valid SignupReqDto signupReqDto,

        @NotBlank String registrationToken
) {}