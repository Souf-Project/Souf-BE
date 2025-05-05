package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.entity.Member;

import java.time.LocalDate;

public record UserResDto(
        Long id,
        String email,
        String username,
        String nickname,
        LocalDate birth,
        String intro,
        RoleType role

) {
    public static UserResDto from(Member member) {
        return new UserResDto(
                member.getId(),
                member.getEmail(),
                member.getUsername(),
                member.getNickname(),
                member.getBirth(),
                member.getIntro(),
                member.getRole()
        );
    }
}
