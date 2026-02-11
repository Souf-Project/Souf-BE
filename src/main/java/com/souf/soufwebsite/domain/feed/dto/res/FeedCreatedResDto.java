package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoDto;

import java.util.List;

public record FeedCreatedResDto(
        Long feedId,
        List<PresignedUrlResDto> dtoList,
        VideoDto videoDto
) {

}
