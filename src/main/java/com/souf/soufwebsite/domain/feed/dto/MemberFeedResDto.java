package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import org.springframework.data.domain.Page;

public record MemberFeedResDto(
        MemberResDto memberResDto,
        Page<FeedSimpleResDto> feedSimpleResDtoPage
) {

}
