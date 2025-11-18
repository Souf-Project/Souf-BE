package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.NOT_ACCEPTED_MEMBER;

public class NotAcceptedMemberException extends BaseErrorException {
    public NotAcceptedMemberException() {
        super(NOT_ACCEPTED_MEMBER.getCode(), NOT_ACCEPTED_MEMBER.getMessage(), NOT_ACCEPTED_MEMBER.getErrorKey());
    }
}
