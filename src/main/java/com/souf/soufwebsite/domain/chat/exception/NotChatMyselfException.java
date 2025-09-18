package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class NotChatMyselfException extends BaseErrorException {
    public NotChatMyselfException() {
        super(ErrorType.NOT_CHAT_MYSELF.getCode(), ErrorType.NOT_CHAT_MYSELF.getMessage(), ErrorType.NOT_CHAT_MYSELF.getErrorKey());
    }
}
