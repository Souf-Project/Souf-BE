package com.souf.soufwebsite.domain.comment.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentSuccessMessage {

    CREATE_COMMENT_SUCCESS("댓글을 성공적으로 작성했습니다."),
    READ_COMMENTS_SUCCESS("댓글을 성공적으로 조회했습니다."),
    UPDATE_COMMENT_SUCCESS("댓글을 성공적으로 수정하였습니다."),
    DELETE_COMMENT_SUCCESS("댓글을 성공적으로 삭제하였습니다.");

    private final String message;
}
