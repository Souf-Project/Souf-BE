package com.souf.soufwebsite.domain.member.dto.ReqDto;

public record FavoriteMemberReqDto(
        Long fromMemberId,
        Long toMemberId
) {
}
