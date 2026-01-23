package com.souf.soufwebsite.domain.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_COMMENT(404, "해당 댓글을 찾을 수 없습니다.", "CM404-1"),
    NOT_MATCHED_OWNER(404, "해당 댓글의 소유자가 아닙니다.", "CM404-2"),
    NOT_MATCHED_COMMENT_AND_FEED(404, "댓글을 소유한 피드의 아이디와 요청된 피드의 아이디가 일치하지 않습니다.", "CM404-3"),

    /* ====================================== 409 ====================================== */
    NOT_REPLY_TO_REPLY(409, "대댓글에는 대댓글을 달 수 없습니다.", "CM409-1");


    private final int code;
    private final String message;
    private final String errorKey;
}
