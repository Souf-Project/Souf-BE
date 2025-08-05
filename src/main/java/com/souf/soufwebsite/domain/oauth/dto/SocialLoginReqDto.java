package com.souf.soufwebsite.domain.oauth.dto;

import com.souf.soufwebsite.domain.oauth.SocialProvider;

public record SocialLoginReqDto(
        String code,
        SocialProvider provider
) {
}
