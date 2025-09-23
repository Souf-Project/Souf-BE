package com.souf.soufwebsite.domain.recruit.dto.req;

import com.souf.soufwebsite.domain.recruit.dto.SortOption;
import com.souf.soufwebsite.domain.recruit.entity.RecruitSortKey;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record RecruitSearchReqDto(
        @Schema(description = "제목") String title,
        @Schema(description = "내용") String content,
        List<CategoryDto> categories,
        SortOption<RecruitSortKey> sortOption
) {}