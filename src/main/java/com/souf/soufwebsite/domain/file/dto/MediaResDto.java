package com.souf.soufwebsite.domain.file.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.file.entity.Media;

public record MediaResDto(
        String fileName,
        String fileUrl
) {
    public static MediaResDto fromFeedThumbnail(Feed feed) {
        return new MediaResDto(
                feed.getMedia().stream()
                        .findFirst()
                        .map(Media::getFileName)
                        .orElse(null),
                feed.getMedia().stream()
                        .findFirst()
                        .map(Media::getOriginalUrl)
                        .orElse(null)
        );
    }

    public static MediaResDto fromFeedDetail(Media media){
        return new MediaResDto(
                media.getFileName(),
                media.getOriginalUrl()
        );
    }
}
