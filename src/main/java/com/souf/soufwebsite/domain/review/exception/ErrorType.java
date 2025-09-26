package com.souf.soufwebsite.domain.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_VALID_AUTHENTICATION(403, "해당 후기를 수정할 권한이 없습니다.", "RV403-1"),
    NOT_FOUND_REVIEW(404, "해당 후기를 찾을 수 없습니다.", "RV404-1");

    private final int code;
    private final String message;
    private final String errorKey;
}
