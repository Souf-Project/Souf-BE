package com.souf.soufwebsite.domain.recruit.dto.req;

import com.souf.soufwebsite.domain.recruit.dto.SortOption;
import com.souf.soufwebsite.domain.recruit.entity.RecruitSortKey;
import io.swagger.v3.oas.annotations.media.Schema;

public record RecruitSearchReqDto(
        @Schema(description = "제목") String title,
        @Schema(description = "내용") String content,
        SortOption<RecruitSortKey> sortOption
) {}