package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.chat.exception.ErrorType.NOT_FOUND_CHAT_MESSAGE;


public class NotFoundChatMessageException extends BaseErrorException {
    public NotFoundChatMessageException() {
        super(NOT_FOUND_CHAT_MESSAGE.getCode(), NOT_FOUND_CHAT_MESSAGE.getMessage());
    }
}
