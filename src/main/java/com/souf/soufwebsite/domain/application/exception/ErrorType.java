package com.souf.soufwebsite.domain.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    ALREADY_APPLIED      (409, "이미 지원한 공고입니다."),
    NOT_FOUND_RECRUIT    (404, "해당 공고를 찾을 수 없습니다.");

    private final int code;
    private final String message;
}
