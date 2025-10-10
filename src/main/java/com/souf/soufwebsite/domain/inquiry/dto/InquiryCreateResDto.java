package com.souf.soufwebsite.domain.inquiry.dto;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;

import java.util.List;

public record InquiryCreateResDto(
        Long inquiryId,
        List<PresignedUrlResDto> dtoList
) {
}
