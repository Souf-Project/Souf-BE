package com.souf.soufwebsite.domain.file.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_VALID_FILE_TYPE(400, "파일 형식이 올바르지 않습니다.", "FI400-1");

    private final int code;
    private final String message;
    private final String errorKey;
}
