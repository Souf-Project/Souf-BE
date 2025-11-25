package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.NOT_EXISTS_INVITE_TOKEN;

public class NotExistsInviteTokenException extends BaseErrorException {
    public NotExistsInviteTokenException() {
        super(NOT_EXISTS_INVITE_TOKEN.getCode(), NOT_EXISTS_INVITE_TOKEN.getMessage(), NOT_EXISTS_INVITE_TOKEN.getErrorKey());
    }
}
