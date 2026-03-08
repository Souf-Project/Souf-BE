package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.FeedCategoryMapping;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FeedSummaryDto(
        Long feedId,
        String topic,
        String content,
        Long viewCount,
        int likedCount,
        int commentCount,
        List<CategoryDto> feedCategoryDtos,
        LocalDateTime createTime
) {

    public static FeedSummaryDto from(Feed feed) {
        return new FeedSummaryDto(
                feed.getId(),
                feed.getTopic(),
                feed.getContent(),
                feed.getViewCount(),
                feed.getLikedCount(),
                feed.getCommentCount(),
                convertToCategoryDto(feed.getCategories()),
                feed.getCreatedTime()
        );
    }

    private static List<CategoryDto> convertToCategoryDto(List<FeedCategoryMapping> mappings){
        return mappings.stream().map(
                m -> new CategoryDto(
                        m.getFirstCategory().getId(),
                        m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                        m.getThirdCategory() != null ? m.getThirdCategory().getId() : null
                )).collect(Collectors.toList());
    }
}
