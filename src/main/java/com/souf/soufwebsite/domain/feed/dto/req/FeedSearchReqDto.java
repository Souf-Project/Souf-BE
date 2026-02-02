package com.souf.soufwebsite.domain.feed.dto.req;

import com.souf.soufwebsite.domain.feed.entity.FeedSortKey;
import com.souf.soufwebsite.global.common.sort.dto.SortOption;

public record FeedSearchReqDto(
        Long firstCategory,
        SortOption<FeedSortKey> sortOption
) {}
