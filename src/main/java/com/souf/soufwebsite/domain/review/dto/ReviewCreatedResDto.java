package com.souf.soufwebsite.domain.review.dto;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;

import java.util.List;

public record ReviewCreatedResDto(
        Long reviewId,
        List<PresignedUrlResDto> dtoList
) {
}
