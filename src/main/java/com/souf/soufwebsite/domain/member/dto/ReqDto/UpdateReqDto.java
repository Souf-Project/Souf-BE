package com.souf.soufwebsite.domain.member.dto.ReqDto;

public record UpdateReqDto(
        String username,
        String nickname,
        String intro
) {
}