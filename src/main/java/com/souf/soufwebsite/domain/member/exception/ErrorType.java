package com.souf.soufwebsite.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_MEMBER(404, "해당 사용자를 찾을 수 없습니다.");

    private final int code;
    private final String message;
}
