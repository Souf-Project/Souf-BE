package com.souf.soufwebsite.domain.socialAccount.dto;

public record SocialPrefill(
        String email,
        String name,
        String profileImageUrl,
        String provider
) {}
