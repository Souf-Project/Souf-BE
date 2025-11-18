package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.ALREADY_CONSUMED_INVITE_TOKEN;

public class AlreadyConsumedInviteTokenException extends BaseErrorException {
    public AlreadyConsumedInviteTokenException() {
        super(ALREADY_CONSUMED_INVITE_TOKEN.getCode(), ALREADY_CONSUMED_INVITE_TOKEN.getMessage(), ALREADY_CONSUMED_INVITE_TOKEN.getErrorKey());
    }
}
