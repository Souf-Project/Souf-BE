package com.souf.soufwebsite.domain.socialAccount.dto;

import com.souf.soufwebsite.domain.socialAccount.SocialProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SocialLinkReqDto(
        @NotNull SocialProvider provider,
        @NotBlank String code
) {}