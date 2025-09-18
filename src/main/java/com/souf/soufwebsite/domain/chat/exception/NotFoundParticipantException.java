package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class NotFoundParticipantException extends BaseErrorException {
    public NotFoundParticipantException() {
        super(ErrorType.NOT_FOUND_PARTICIPANT.getCode(), ErrorType.NOT_FOUND_PARTICIPANT.getMessage(), ErrorType.NOT_FOUND_PARTICIPANT.getErrorKey());
    }
}
