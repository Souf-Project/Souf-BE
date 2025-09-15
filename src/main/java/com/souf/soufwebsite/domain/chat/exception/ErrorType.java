package com.souf.soufwebsite.domain.chat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    NOT_FOUND_CHAT_MESSAGE(404, "해당 채팅을 수정할 찾을 수 없습니다!", "C404-1");

    private final int code;
    private final String message;
    private final String errorKey;
}