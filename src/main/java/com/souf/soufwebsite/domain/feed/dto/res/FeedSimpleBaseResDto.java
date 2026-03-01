package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSummaryDto;

import java.time.LocalDateTime;

public record FeedSimpleBaseResDto(

        MemberSummaryDto writer,
        String writerProfileUrl,

        Long feedId,
        String topic,
        String content,
        int likedCount,
        Long viewCount,
        LocalDateTime createdAt,

        MediaResDto mediaResDto
) {

    public static FeedSimpleBaseResDto of(MemberSummaryDto memberSummaryDto, String profileUrl, Feed feed, Long viewCount, MediaResDto resDto) {
        return new FeedSimpleBaseResDto(

                memberSummaryDto,
                profileUrl,
                feed.getId(),
                feed.getTopic(),
                feed.getContent(),
                feed.getLikedCount(),
                viewCount,
                feed.getCreatedTime(),
                resDto
        );
    }
}
