package com.souf.soufwebsite.domain.socialAccount.dto.google;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserResDto(
        @JsonProperty("sub")
        String id,
        String name,
        String email,
        String picture
) {
}
