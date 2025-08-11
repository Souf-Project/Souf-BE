package com.souf.soufwebsite.domain.socialAccount.dto;

import com.souf.soufwebsite.domain.socialAccount.SocialProvider;
import io.swagger.v3.oas.annotations.media.Schema;

public record SocialLoginReqDto(
        @Schema(description = "소셜 로그인에 사용되는 코드", example = "1234567890abcdef")
        String code,
        @Schema(description = "소셜 로그인 제공자", example = "KAKAO")
        SocialProvider provider
) {
}
