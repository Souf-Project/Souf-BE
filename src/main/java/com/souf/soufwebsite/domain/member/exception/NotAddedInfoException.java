package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_ADDED_INFO;

public class NotAddedInfoException extends BaseErrorException {
    public NotAddedInfoException() {
        super(NOT_ADDED_INFO.getCode(), NOT_ADDED_INFO.getMessage(), NOT_ADDED_INFO.getErrorKey());
    }
}
