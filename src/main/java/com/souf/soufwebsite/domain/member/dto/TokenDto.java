package com.souf.soufwebsite.domain.member.dto;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import lombok.Builder;

@Builder
public record TokenDto(
        String accessToken,
        String refreshToken,
        Long memberId,
        String username,
        RoleType roleType
) {
}
