package com.souf.soufwebsite.domain.member.dto;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import lombok.Builder;

@Builder
public record TokenDto(
        String accessToken,
        Long memberId,
        String nickname,
        RoleType roleType
) {
}
