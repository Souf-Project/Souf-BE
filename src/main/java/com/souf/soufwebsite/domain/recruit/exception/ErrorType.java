package com.souf.soufwebsite.domain.recruit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    _NOT_VALID_AUTHENTICATION(403, "해당 공고를 수정할 권한이 없습니다!"),
    _NOT_FOUND_RECRUIT(404, "해당 공고를 찾을 수 없습니다."),
    _NOT_VALID_DEADLINE(404, "유효하지 않은 날짜입니다.");

    private final int code;
    private final String message;
}
