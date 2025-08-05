package com.souf.soufwebsite.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CommentReqDto(

        @Schema(description = "댓글 작성자의 아이디를 반환해주세요.", example = "1")
        @NotNull
        Long writerId,

        @Schema(description = "댓글 내용을 입력해주세요", example = "안녕하세요")
        @NotNull
        String content,

        @Schema(description = "피드 소유자의 아이디를 반환해주세요", example = "2")
        @NotNull
        Long authorId,

        @Schema(description = "피드의 대댓글을 위한 댓글 아이디를 입력해주세요", example = "3")
        Long parentId
) {
}
