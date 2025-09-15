package com.souf.soufwebsite.domain.comment.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_COMMENT(404, "해당 댓글을 찾을 수 없습니다.", "C404-1"),
    NOT_MATCHED_OWNER(404, "해당 댓글의 소유자가 아닙니다.", "C404-2");

    private final int code;
    private final String message;
    private final String errorKey;
}
