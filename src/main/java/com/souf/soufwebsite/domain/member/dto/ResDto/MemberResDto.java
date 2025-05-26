package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

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

        @Schema(description = "회원 권한", example = "MEMBER")
        RoleType role

) {
    public static MemberResDto from(Member member) {
        return new MemberResDto(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getNickname(),
                member.getIntro(),
                member.getRole()
        );
    }
}
