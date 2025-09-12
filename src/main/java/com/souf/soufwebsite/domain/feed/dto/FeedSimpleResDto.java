package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;

import java.util.List;
import java.util.stream.Collectors;

public record FeedSimpleResDto(
        Long feedId,
        String title,
        Long memberId,
        String nickname,
        List<Long> firstCategories,
        MediaResDto mediaResDto
) {
    public static FeedSimpleResDto from(Feed feed , MediaResDto mediaResDto) {

        List<Long> firstCategoryIds = feed.getCategories().stream()
                .map(mapping -> mapping.getFirstCategory().getId())
                .distinct()
                .collect(Collectors.toList());

        return new FeedSimpleResDto(
                feed.getId(),
                feed.getTopic(),
                feed.getMember().getId(),
                feed.getMember().getNickname(),
                firstCategoryIds,
                mediaResDto);
    }
}