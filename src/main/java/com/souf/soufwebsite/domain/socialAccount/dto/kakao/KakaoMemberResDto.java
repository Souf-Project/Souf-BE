package com.souf.soufwebsite.domain.socialAccount.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoMemberResDto(
        String id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public record KakaoAccount(
            String email,
            KakaoProfile profile
    ) {}

    public record KakaoProfile(
            String nickname,
            @JsonProperty("profile_image_url") String profileImageUrl
    ) {}
}