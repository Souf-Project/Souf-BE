package com.souf.soufwebsite.domain.oauth.dto;

public record SocialMemberInfo(
        String socialId,
        String email,
        String nickname,
        String profileImageUrl
) {
}
