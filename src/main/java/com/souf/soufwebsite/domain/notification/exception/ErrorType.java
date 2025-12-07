package com.souf.soufwebsite.domain.notification.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_NOTIFICATION(404, "해당 알림을 찾을 수 없습니다.", "N404-1");

    private final int code;
    private final String message;
    private final String errorKey;
}
