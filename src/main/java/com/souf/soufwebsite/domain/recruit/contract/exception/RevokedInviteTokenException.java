package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.REVOKED_INVITE_TOKEN;

public class RevokedInviteTokenException extends BaseErrorException {
    public RevokedInviteTokenException() {
        super(REVOKED_INVITE_TOKEN.getCode(), REVOKED_INVITE_TOKEN.getMessage(), REVOKED_INVITE_TOKEN.getErrorKey());
    }
}
