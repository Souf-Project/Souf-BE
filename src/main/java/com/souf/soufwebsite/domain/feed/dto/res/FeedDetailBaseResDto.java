package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.entity.Member;

import java.util.List;

public record FeedDetailBaseResDto(
        Member m,
        String profileUrl,
        Feed f,
        Long totalViewCount,
        List<Media> mediaList
) {

    public static FeedDetailBaseResDto from(Member currentMember, String profileUrl, Feed f, Long totalViewCount, List<Media> mediaList) {
        return new FeedDetailBaseResDto(currentMember, profileUrl, f, totalViewCount, mediaList);
    }
}
