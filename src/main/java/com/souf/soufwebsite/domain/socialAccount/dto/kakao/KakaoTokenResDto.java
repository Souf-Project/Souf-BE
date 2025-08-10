package com.souf.soufwebsite.domain.socialAccount.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResDto(
        @JsonProperty("access_token")
        String accessToken
) {
}
