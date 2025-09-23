package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.chat.exception.ErrorType.NOT_FOUND_CHAT_ROOM;

public class NotFoundChatRoomException extends BaseErrorException {
    public NotFoundChatRoomException() {
        super(NOT_FOUND_CHAT_ROOM.getCode(), NOT_FOUND_CHAT_ROOM.getMessage(), NOT_FOUND_CHAT_ROOM.getErrorKey());
    }
}
