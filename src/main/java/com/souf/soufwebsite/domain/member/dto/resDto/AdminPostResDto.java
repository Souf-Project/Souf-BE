package com.souf.soufwebsite.domain.member.dto.resDto;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.global.common.PostType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record AdminPostResDto(

        @Schema(description = "글 타입", example = "FEED, RECRUIT, COMMENT, PROFILE")
        PostType type,
        @Schema(description = "게시글 아이디", example = "1")
        Long postId,
        @Schema(description = "글 제목(댓글은 내용)", example = "feedTitle")
        String title,

        @Schema(description = "작성자 아이디", example = "1")
        Long writerId,
        @Schema(description = "작성자", example = "미나리나무")
        String writer,
        @Schema(description = "생성날짜", example = "2025-08-21")
        LocalDate createdDate
) {
        public static AdminPostResDto fromFeed(Feed feed){
                return new AdminPostResDto(
                        PostType.FEED,
                        feed.getId(),
                        feed.getTopic(),
                        feed.getMember().getId(),
                        feed.getMember().getNickname(),
                        feed.getCreatedTime().toLocalDate()
                );
        }

        public static AdminPostResDto fromRecruit(Recruit recruit){
                return new AdminPostResDto(
                        PostType.RECRUIT,
                        recruit.getId(),
                        recruit.getTitle(),
                        recruit.getMember().getId(),
                        recruit.getMember().getNickname(),
                        recruit.getCreatedTime().toLocalDate()
                );
        }
}
