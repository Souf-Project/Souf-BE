package com.souf.soufwebsite.domain.feed.dto.res;

import com.souf.soufwebsite.domain.feed.dto.FeedSummaryDto;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberSummaryDto;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record FeedDetailResDto(

        Long memberId,
        String nickname,
        String profileImageUrl,
        String intro,
        double temperature,
        List<CategoryDto> studentCategories,

        Long feedId,
        String topic,
        String content,
        Long view,
        int likedCount,

        @Schema(description = "좋아요를 피드 작성자가 눌렀다면 TRUE, 아니라면 FALSE를 반환합니다.", example = "TRUE")
        Boolean liked, //  자신이 좋아요를 눌렀는지 여부 확인
        int commentCount,
        List<MediaResDto> mediaResDtos,
        List<CategoryDto> categoryDtos,
        LocalDateTime createdTime
) {
    public static FeedDetailResDto from(FeedDetailBaseResDto resDto,
                                        Long totalViewCount,
                                        Boolean liked) {

        MemberSummaryDto member = resDto.m();
        FeedSummaryDto feed = resDto.feedSummaryDto();

        return new FeedDetailResDto(
                member.id(),
                member.nickname(),
                resDto.profileUrl(),
                member.intro(),
                member.temperature(),
                member.memberCategories(),

                feed.feedId(),
                feed.topic(),
                feed.content(),
                totalViewCount,
                feed.likedCount(),
                liked,
                feed.commentCount(),
                resDto.mediaList(),
                feed.feedCategoryDtos(),
                feed.createTime());
    }
}
