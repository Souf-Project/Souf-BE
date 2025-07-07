package com.souf.soufwebsite.domain.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record FeedSearchReqDto(

        @Schema(description = "제목을 입력해주세요.(String)")
        String title,

        @Schema(description = "내용을 입력해주세요.(String)")
        String content
) {
}
