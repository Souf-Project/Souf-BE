package com.souf.soufwebsite.domain.inquiry.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_VALID_AUTHENTICATION(401, "해당 문의글를 수정할 권한이 없습니다.", "I401-1"),
    NOT_FOUND_INQUIRY(404, "해당 문의글를 찾을 수 없습니다.", "I404-1");


    private final int code;
    private final String message;
    private final String errorKey;
}
