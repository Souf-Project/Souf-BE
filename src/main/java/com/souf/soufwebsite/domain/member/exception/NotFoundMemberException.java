package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_FOUND_MEMBER;

public class NotFoundMemberException extends BaseErrorException {

    public NotFoundMemberException() {
        super(NOT_FOUND_MEMBER.getCode(), NOT_FOUND_MEMBER.getMessage());
    }
}
