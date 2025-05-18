package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FeedResDto(
        String topic,
        String content,
        int view,
        List<MediaResDto> mediaResDtos,
        LocalDateTime lastModifiedTime
) {
    public static FeedResDto from(Feed feed){
        return new FeedResDto(
                feed.getTopic(),
            feed.getContent(),
            feed.getViewCount(),
            convertToMediaResDto(feed.getMedia()),
            feed.getLastModifiedTime());
    }

    private static List<MediaResDto> convertToMediaResDto(List<Media> mediaList){
        return mediaList.stream().map(
                media -> MediaResDto.fromFeedDetail(media)
        ).collect(Collectors.toList());
    }
}
