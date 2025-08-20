package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.file.entity.PostType;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdminPostResDto(

        @Schema(description = "글 타입", example = "FEED, RECRUIT, COMMENT, PROFILE")
        PostType type,

        @Schema(description = "작성자", example = "미나리나무")
        String writer,
        @Schema(description = "글 제목(댓글은 내용)", example = "feedTitle")
        String title,
        @Schema(description = "생성날짜", example = "2025-08-21")
        String createdDate
) {
}
