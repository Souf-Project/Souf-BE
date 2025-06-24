package com.souf.soufwebsite.domain.member.dto.ResDto;

import java.util.List;

public record MemberSimpleResDto(
        Long memberId,
        String profileImageUrl,
        String nickname,
        String intro,
        List<PopularFeedDto> popularFeeds
) {
    public record PopularFeedDto(
            String imageUrl
    ) {}
}
