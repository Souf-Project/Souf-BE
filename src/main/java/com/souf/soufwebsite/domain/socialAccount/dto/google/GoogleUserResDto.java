package com.souf.soufwebsite.domain.socialAccount.dto.google;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleUserResDto(
        @JsonProperty("sub")
        String id,

        @JsonProperty("name")
        String name,

        @JsonProperty("email")
        String email,

        @JsonProperty("picture")
        String picture
) {}