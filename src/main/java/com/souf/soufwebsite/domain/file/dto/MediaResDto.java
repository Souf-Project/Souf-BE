package com.souf.soufwebsite.domain.file.dto;

import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.MediaType;

import java.util.List;

import static com.souf.soufwebsite.domain.file.entity.MediaType.*;

public record MediaResDto(
        String fileName,
        String fileUrl
) {

    public static MediaResDto fromFeedDetail(Media media){
        String originalUrl = media.getOriginalUrl();

        return new MediaResDto(
                media.getFileName(),
                originalUrl
        );
    }

    public static MediaResDto fromRecruit(Media media) {
        MediaType type = media.getMediaType();

        if (type.isDocumentOrEtc()) {
            return null;
        }

        if (type.needsThumbnail()) {
            String url = (media.getThumbnailUrl() != null && !media.getThumbnailUrl().isBlank())
                    ? media.getThumbnailUrl()
                    : media.getOriginalUrl();
            return new MediaResDto(media.getFileName(), url);
        }

        return new MediaResDto(media.getFileName(), media.getOriginalUrl());
    }

    public static MediaResDto fromMedia(Media media){
        List<MediaType> mediaTypes = List.of(new MediaType[]{MP4, MOV, AVI, MKV, WEBM, FLV, QUICKTIME});
        String originalUrl = media.getOriginalUrl();
        if(mediaTypes.contains(media.getMediaType())){
            originalUrl = media.getThumbnailUrl();
        }

        return new MediaResDto(
                media.getFileName(),
                originalUrl
        );
    }
}
