package com.souf.soufwebsite.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CommentUpdateReqDto(

        @Schema(description = "수정할 댓글의 아이디를 알려주세요", example = "1")
        Long commentId,

        @Schema(description = "수정을 시도하는 사용자의 아이디를 알려주세요", example = "1")
        Long writerId,

        @Schema(description = "수정할 내용을 넣어주세요(삭제 API 시 안 넣으면 됨", example = "updated content")
        String content
) {
}
