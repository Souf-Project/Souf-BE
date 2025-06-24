package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;

public record FeedSimpleResDto(
        Long feedId,
        String nickname,
        String categoryName,
        MediaResDto mediaResDto
) {
    public static FeedSimpleResDto from(Feed feed , MediaResDto mediaResDto) {
        String nickname = feed.getMember().getNickname();
        String categoryName = feed.getCategories().stream()
                .findFirst()
                .map(cat -> cat.getSecondCategory().getName())
                .orElse(null);

        return new FeedSimpleResDto(feed.getId(), nickname, categoryName, mediaResDto);
    }
}