package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.feed.dto.FeedSummaryDto;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSummaryDto;

import java.util.List;

public record FeedDetailBaseResDto(
        MemberSummaryDto m,
        String profileUrl,

        FeedSummaryDto feedSummaryDto,

        List<MediaResDto> mediaList
) {

    public static FeedDetailBaseResDto from(MemberSummaryDto memberSummaryDto, String profileUrl, FeedSummaryDto feedSummaryDto, List<MediaResDto> mediaList) {
        return new FeedDetailBaseResDto(memberSummaryDto, profileUrl, feedSummaryDto, mediaList);
    }
}
