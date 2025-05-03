package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.FileDto;

import java.util.List;

public record FeedResDto (
        Long feedId,
        String content,
        List<FileDto> files,
        String nickname
) {
    public static FeedResDto from(Feed feed, String nickname) {
        return new FeedResDto(feed.getId(),
                feed.getContent(),
                feed.getFiles().stream()
                        .map(FileDto::from)
                        .toList(),
                nickname
        );
    }
}