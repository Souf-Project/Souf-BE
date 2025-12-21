package com.souf.soufwebsite.domain.socialAccount.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleTokenResDto(
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("expires_in")
        Long expiresIn,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("scope")
        String scope

) {}