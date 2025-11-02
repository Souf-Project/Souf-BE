package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.INVALID_JOIN_DECISION;

public class InvalidJoinDecisionException extends BaseErrorException {
    public InvalidJoinDecisionException() {
        super(INVALID_JOIN_DECISION.getCode(), INVALID_JOIN_DECISION.getMessage(), INVALID_JOIN_DECISION.getErrorKey());
    }
}
