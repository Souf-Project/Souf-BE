package com.souf.soufwebsite.domain.socialAccount.dto;

import com.souf.soufwebsite.domain.member.dto.TokenDto;

public record SocialLoginResDto(
        boolean requiresSignup,          // 온보딩 필요 여부
        boolean requiresLink,            // 계정 연동 필요 여부
        String registrationToken,
        TokenDto token,                  // 로그인 성공 시만 채움
        String message,        // 온보딩 필요 시만 채움
        SocialPrefill prefill            // 프리필용 (이메일/닉네임 후보/프로필 등)
) {
    public static SocialLoginResDto loggedIn(TokenDto token, SocialPrefill prefill) {
        return new SocialLoginResDto(false, false, null, token, null, prefill);
    }

    public static SocialLoginResDto requiresSignup(String message, SocialPrefill prefill) {
        return new SocialLoginResDto(false, true, null, null, message, prefill);
    }

    public static SocialLoginResDto requiresLink(String registrationToken, SocialPrefill prefill) {
        return new SocialLoginResDto(true, false, registrationToken, null, null, prefill);
    }
}