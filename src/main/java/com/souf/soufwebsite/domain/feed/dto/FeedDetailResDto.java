package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.FeedCategoryMapping;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FeedDetailResDto(

        Long memberId,
        String nickname,
        String profileUrl,
        Long feedId,
        String topic,
        String content,
        Long view,
        List<MediaResDto> mediaResDtos,
        List<CategoryDto> categoryDtos,
        LocalDateTime lastModifiedTime
) {
    public static FeedDetailResDto from(Member member, String profileUrl, Feed feed, Long feedViewCount, List<Media> mediaList) {
        return new FeedDetailResDto(
                member.getId(),
                member.getNickname(),
                profileUrl,
                feed.getId(),
                feed.getTopic(),
            feed.getContent(),
            feed.getViewCount() + feedViewCount,
            convertToMediaResDto(mediaList),
            convertToCategoryDto(feed.getCategories()),
            feed.getLastModifiedTime());
    }

    private static List<MediaResDto> convertToMediaResDto(List<Media> mediaList){
        return mediaList.stream().map(
                MediaResDto::fromFeedDetail
        ).collect(Collectors.toList());
    }

    private static List<CategoryDto> convertToCategoryDto(List<FeedCategoryMapping> mappings){
        return mappings.stream().map(
                m -> new CategoryDto(m.getFirstCategory().getId(), m.getSecondCategory().getId(), m.getThirdCategory().getId())
        ).collect(Collectors.toList());
    }
}
