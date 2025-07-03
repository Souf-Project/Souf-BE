package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.VideoResDto;

import java.util.List;

public record FeedResDto(
        Long feedId,
        List<PresignedUrlResDto> dtoList,
        VideoResDto videoResDto
) {

}
