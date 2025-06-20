package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

public record MemberResDto(
        Long id,

        @Schema(description = "이메일 주소", example = "user@example.com")
        String email,

        @Schema(description = "사용자 실명", example = "홍길동")
        String username,

        @Schema(description = "닉네임", example = "김개똥")
        String nickname,

        @Schema(description = "자기소개", example = "안녕하세요, 디자인 전공자입니다.")
        String intro,

        @Schema(description = "개인 URL", example = "https://github.com/username")
        String personalUrl,

        @Schema(description = "회원 권한", example = "MEMBER")
        RoleType role,

        @Schema(description = "회원 프로필 사진", example = "sakdjffasljk.png")
        String profileUrl

) {
    public static MemberResDto from(Member member, String profileUrl) {
        return new MemberResDto(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getNickname(),
                member.getIntro(),
                member.getPersonalUrl(),
                member.getRole(),
                profileUrl
        );
    }
}
