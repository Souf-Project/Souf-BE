package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class NotFoundChatRoomException extends BaseErrorException {
    public NotFoundChatRoomException() {
        super(ErrorType.NOT_FOUND_CHAT_ROOM.getCode(), ErrorType.NOT_FOUND_CHAT_ROOM.getMessage(), ErrorType.NOT_FOUND_CHAT_ROOM.getErrorKey());
    }
}
