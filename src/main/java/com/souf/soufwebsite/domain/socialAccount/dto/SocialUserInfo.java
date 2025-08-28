package com.souf.soufwebsite.domain.socialAccount.dto;

public record SocialUserInfo(
        String socialId,
        String email,
        String name,
        String profileImageUrl
) {
}
