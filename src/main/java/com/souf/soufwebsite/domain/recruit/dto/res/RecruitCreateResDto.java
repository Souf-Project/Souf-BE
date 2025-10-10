package com.souf.soufwebsite.domain.recruit.dto.res;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoDto;

import java.util.List;

public record RecruitCreateResDto(
        Long recruitId,
        List<PresignedUrlResDto> dtoList,
        PresignedUrlResDto logoPresignedUrlResDto,
        VideoDto videoDto
) {
}
