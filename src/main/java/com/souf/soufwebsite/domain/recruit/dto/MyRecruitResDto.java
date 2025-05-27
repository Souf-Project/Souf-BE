package com.souf.soufwebsite.domain.recruit.dto;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.time.LocalDateTime;
import java.util.List;

public record MyRecruitResDto(
        Long recruitId,
        String title,
        LocalDateTime deadline,
        List<CategoryDto> categoryDtos,
        String status,
        long recruitCount
) {
}
