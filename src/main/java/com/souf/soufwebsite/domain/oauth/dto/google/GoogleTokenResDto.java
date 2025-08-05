package com.souf.soufwebsite.domain.oauth.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResDto(
        @JsonProperty("access_token")
        String accessToken
) {
}
