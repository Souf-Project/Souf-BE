package com.souf.soufwebsite.domain.socialAccount.dto;

import com.souf.soufwebsite.domain.member.dto.TokenDto;

public record SocialLoginResDto(
        boolean requiresSignup,          // 온보딩 필요 여부
        TokenDto token,                  // 로그인 성공 시만 채움
        String registrationToken,        // 온보딩 필요 시만 채움
        SocialPrefill prefill            // 프리필용 (이메일/닉네임 후보/프로필 등)
) {}