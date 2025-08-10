package com.souf.soufwebsite.domain.socialAccount.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResDto(
        @JsonProperty("access_token")
        String accessToken
) {
}
