package com.souf.soufwebsite.domain.oauth.dto.google;

public record GoogleMemberResDto(
        String id,
        String email,
        String name,
        String picture
) {
}
