package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;

public record FeedSimpleResDto(
        Long feedId,

        MediaResDto mediaResDto
) {
    public static FeedSimpleResDto from(Feed feed , MediaResDto mediaResDto) {
        return new FeedSimpleResDto(feed.getId(),
                mediaResDto
        );
    }
}