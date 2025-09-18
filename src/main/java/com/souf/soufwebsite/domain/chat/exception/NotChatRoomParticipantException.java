package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.chat.exception.ErrorType.NOT_CHAT_ROOM_PARTICIPANT;

public class NotChatRoomParticipantException extends BaseErrorException {
    public NotChatRoomParticipantException() {
        super(NOT_CHAT_ROOM_PARTICIPANT.getCode(), NOT_CHAT_ROOM_PARTICIPANT.getMessage(), NOT_CHAT_ROOM_PARTICIPANT.getErrorKey());
    }
}
