package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.ALREADY_EXPIRED_INVITE_TOKEN;

public class AlreadyExpiredInviteTokenException extends BaseErrorException {
    public AlreadyExpiredInviteTokenException() {
        super(ALREADY_EXPIRED_INVITE_TOKEN.getCode(), ALREADY_EXPIRED_INVITE_TOKEN.getMessage(), ALREADY_EXPIRED_INVITE_TOKEN.getErrorKey());
    }
}
