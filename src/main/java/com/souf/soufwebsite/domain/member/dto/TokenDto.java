package com.souf.soufwebsite.domain.member.dto;

import lombok.Builder;

@Builder
public record TokenDto(
        String accessToken,
        String refreshToken
) {
}
