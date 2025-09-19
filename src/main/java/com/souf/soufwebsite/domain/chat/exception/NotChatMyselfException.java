package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.chat.exception.ErrorType.NOT_CHAT_MYSELF;

public class NotChatMyselfException extends BaseErrorException {
    public NotChatMyselfException() {
        super(NOT_CHAT_MYSELF.getCode(), NOT_CHAT_MYSELF.getMessage(), NOT_CHAT_MYSELF.getErrorKey());
    }
}
