package com.souf.soufwebsite.domain.recruit.dto.req;

import com.souf.soufwebsite.domain.recruit.dto.SortOption;
import com.souf.soufwebsite.domain.recruit.entity.MyRecruitSortKey;


public record MyRecruitReqDto(
        SortOption<MyRecruitSortKey> sortOption
) {
    public MyRecruitReqDto {
        if (sortOption == null) {
            sortOption = new SortOption<>(MyRecruitSortKey.RECENT, SortOption.SortDir.DESC);
        } else if (sortOption.sortKey() == null) {
            sortOption = new SortOption<>(MyRecruitSortKey.RECENT, sortOption.sortDir());
        }
    }
}