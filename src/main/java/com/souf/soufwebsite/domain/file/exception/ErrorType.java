package com.souf.soufwebsite.domain.file.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_VALID_FILE_TYPE(400, "파일 형식이 올바르지 않습니다.", "FI400-1"),
    NOT_REQUESTED_FILE(400, "파일이 반드시 제출되어야 합니다.", "FI400-2"),

    NOT_FOUND_MEDIA(404, "originalUrl에 대한 Media를 찾을 수 없습니다.", "FI404-1");

    private final int code;
    private final String message;
    private final String errorKey;
}
