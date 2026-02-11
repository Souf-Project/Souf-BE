package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import org.springframework.data.domain.Page;

public record MemberFeedResDto(
        MemberResDto memberResDto,
        Page<PopularFeedResDto> feedSimpleResDtoPage
) {

}
