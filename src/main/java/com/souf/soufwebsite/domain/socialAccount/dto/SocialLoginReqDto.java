package com.souf.soufwebsite.domain.socialAccount.dto;

import com.souf.soufwebsite.domain.socialAccount.SocialProvider;

public record SocialLoginReqDto(
        String code,
        SocialProvider provider
) {
}
