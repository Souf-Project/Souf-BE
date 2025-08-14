package com.souf.soufwebsite.domain.socialAccount.dto;

import java.util.List;

public record SocialCompleteSignupReqDto(
        String registrationToken,         // 로그인 단계에서 받은 임시 토큰
        String nickname,                  // 사용자가 최종 선택한 닉네임
        List<Long> categoryIds            // 선택한 카테고리(예시)
) {}
