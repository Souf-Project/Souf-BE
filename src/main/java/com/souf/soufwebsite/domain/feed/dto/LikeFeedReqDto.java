package com.souf.soufwebsite.domain.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LikeFeedReqDto(

        @Schema(description = "member 아이디", example = "1")
        Long memberId,

        @Schema(description = "좋아요 여부", example = "등록이면 TRUE, 삭제면 FALSE")
        Boolean isLiked
) {
}
