package com.souf.soufwebsite.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    _NOT_AUTHORIZED(403, "유효하지 않은 사용자입니다.");

    private final int code;
    private final String message;
}
