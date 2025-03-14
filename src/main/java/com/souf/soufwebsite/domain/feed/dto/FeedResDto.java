package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;

public record FeedResDto (
        Long feedId,
        String content,
        String nickname
) {
    public static FeedResDto from(Feed feed, String nickname) {
        return new FeedResDto(feed.getId(),
                feed.getContent(),
                nickname);
    }
}