package com.souf.soufwebsite.domain.review.dto;

import com.souf.soufwebsite.domain.recruit.dto.SortOption;
import com.souf.soufwebsite.domain.review.entity.ReviewSortKey;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.util.List;

public record ReviewSearchReqDto(
        List<CategoryDto> categories,
        SortOption<ReviewSortKey> sortOption
) {
    public ReviewSearchReqDto {
        if (sortOption == null) {
            sortOption = new SortOption<>(ReviewSortKey.RECENT, SortOption.SortDir.DESC);
        } else if (sortOption.sortKey() == null) {
            sortOption = new SortOption<>(ReviewSortKey.RECENT, sortOption.sortDir());
        }
    }
}
