package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;

import java.util.List;

public record RecruitCreateResDto(
        Long recruitId,
        List<PresignedUrlResDto> dtoList
) {
}
