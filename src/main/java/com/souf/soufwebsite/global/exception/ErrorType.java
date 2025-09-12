package com.souf.soufwebsite.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_AUTHORIZED(403, "유효하지 않은 사용자입니다.", "G403"),

    /* ================================== 해시 ================================== */
    NOT_AVAILABLE_ALGORITHM(500, "SHA-256 알고리즘이 유효하지 않습니다.", "G500");

    private final int code;
    private final String message;
    private final String errorKey;
}
