package com.souf.soufwebsite.domain.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_REVIEW(404, "해당 후기를 찾을 수 없습니다.", "RV404-1");

    private final int code;
    private final String message;
    private final String errorKey;
}
