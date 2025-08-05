package com.souf.soufwebsite.domain.oauth.dto;

public record LoginResDto(
        String accessToken,
        boolean isNewMember
) {
}
