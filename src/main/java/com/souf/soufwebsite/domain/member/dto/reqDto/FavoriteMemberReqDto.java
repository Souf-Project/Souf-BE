package com.souf.soufwebsite.domain.member.dto.reqDto;

public record FavoriteMemberReqDto(
        Long fromMemberId,
        Long toMemberId
) {
}
