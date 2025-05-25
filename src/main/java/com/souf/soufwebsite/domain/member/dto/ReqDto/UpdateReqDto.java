package com.souf.soufwebsite.domain.member.dto.ReqDto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateReqDto(

        @Schema(description = "사용자 실명", example = "홍길동")
        String username,

        @Schema(description = "닉네임", example = "김개똥")
        String nickname,

        @Schema(description = "자기소개", example = "안녕하세요, 디자인 전공자입니다.")
        String intro,

        @Schema(description = "개인 URL", example = "https://github.com/username")
        String personalUrl
) {
}