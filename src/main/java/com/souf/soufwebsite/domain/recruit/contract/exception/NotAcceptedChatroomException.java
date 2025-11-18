package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.NOT_ACCEPTED_CHATROOM;

public class NotAcceptedChatroomException extends BaseErrorException {
    public NotAcceptedChatroomException() {
        super(NOT_ACCEPTED_CHATROOM.getCode(), NOT_ACCEPTED_CHATROOM.getMessage(), NOT_ACCEPTED_CHATROOM.getErrorKey());
    }
}
