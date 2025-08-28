package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.FeedCategoryMapping;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record FeedDetailResDto(

        Long memberId,
        String nickname,
        String profileImageUrl,
        Long feedId,
        String topic,
        String content,
        Long view,
        Long likedCount,

        @Schema(description = "좋아요를 피드 작성자가 눌렀다면 TRUE, 아니라면 FALSE를 반환합니다.", example = "TRUE")
        Boolean liked, //  자신이 좋아요를 눌렀는지 여부 확인
        Long commentCount,
        List<MediaResDto> mediaResDtos,
        List<CategoryDto> categoryDtos,
        LocalDateTime lastModifiedTime
) {
    public static FeedDetailResDto from(Member member, String profileImageUrl, Feed feed, Long feedViewCount,
                                        Long likedCount, Boolean liked, Long commentCount, List<Media> mediaList) {
        return new FeedDetailResDto(
                member.getId(),
                member.getNickname(),
                profileImageUrl,
                feed.getId(),
                feed.getTopic(),
            feed.getContent(),
            feed.getViewCount() + feedViewCount,
            likedCount,
            liked,
            commentCount,
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
                m -> new CategoryDto(
                        m.getFirstCategory().getId(),
                        m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                        m.getThirdCategory() != null ? m.getThirdCategory().getId() : null
        )).collect(Collectors.toList());
    }
}
