package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;

import java.util.List;

public record MemberFeedResDto(
        MemberResDto memberResDto,
        List<PopularFeedResDto> items,
        boolean hasNext
) {
}
