package com.souf.soufwebsite.domain.recruit.dto.req;

import com.souf.soufwebsite.domain.recruit.dto.SortOption;
import com.souf.soufwebsite.domain.recruit.entity.MyRecruitSortKey;


public record MyRecruitReqDto(
        SortOption<MyRecruitSortKey> sortOption
) {}