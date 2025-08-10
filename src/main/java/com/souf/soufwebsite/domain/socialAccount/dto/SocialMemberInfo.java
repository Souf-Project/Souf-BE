package com.souf.soufwebsite.domain.socialAccount.dto;

public record SocialMemberInfo(
        String socialId,
        String email,
        String name,
        String profileImageUrl
) {
}
