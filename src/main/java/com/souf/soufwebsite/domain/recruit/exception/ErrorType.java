package com.souf.soufwebsite.domain.recruit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_VALID_AUTHENTICATION(401, "해당 공고를 수정할 권한이 없습니다!", "R401-1"),
    NOT_FOUND_RECRUIT(404, "해당 공고를 찾을 수 없습니다.", "R404-1"),
    NOT_VALID_DEADLINE(400, "유효하지 않은 날짜입니다.", "R400-1");

    private final int code;
    private final String message;
    private final String errorKey;
}
