package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.chat.exception.ErrorType.NOT_FOUND_PARTICIPANT;

public class NotFoundParticipantException extends BaseErrorException {
    public NotFoundParticipantException() {
        super(NOT_FOUND_PARTICIPANT.getCode(), NOT_FOUND_PARTICIPANT.getMessage(), NOT_FOUND_PARTICIPANT.getErrorKey());
    }
}
