package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FeedDetailResDto(
        String topic,
        String content,
        Long view,
        List<MediaResDto> mediaResDtos,
        LocalDateTime lastModifiedTime
) {
    public static FeedDetailResDto from(Feed feed, Long feedViewCount){
        return new FeedDetailResDto(
                feed.getTopic(),
            feed.getContent(),
            feed.getViewCount() + feedViewCount,
            convertToMediaResDto(feed.getMedia()),
            feed.getLastModifiedTime());
    }

    private static List<MediaResDto> convertToMediaResDto(List<Media> mediaList){
        return mediaList.stream().map(
                MediaResDto::fromFeedDetail
        ).collect(Collectors.toList());
    }
}
