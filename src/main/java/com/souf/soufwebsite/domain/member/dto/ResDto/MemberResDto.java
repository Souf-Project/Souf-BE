package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.entity.Member;

import java.time.LocalDate;

public record MemberResDto(
        Long id,
        String email,
        String username,
        String nickname,
        String intro,
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
