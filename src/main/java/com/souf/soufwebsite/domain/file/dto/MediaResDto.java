package com.souf.soufwebsite.domain.file.dto;

import com.souf.soufwebsite.domain.file.entity.Media;

public record MediaResDto(
        String fileName,
        String fileUrl
) {

    public static MediaResDto fromFeedDetail(Media media){
        return new MediaResDto(
                media.getFileName(),
                media.getOriginalUrl()
        );
    }
}
