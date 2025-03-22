package com.souf.soufwebsite.domain.feed.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    _NOT_VALID_AUTHENTICATION(403, "해당 피드를 수정할 권한이 없습니다!"),
    _NOT_FOUND_FEED(404, "해당 피드를 찾을 수 없습니다.");

    private final int code;
    private final String message;
}