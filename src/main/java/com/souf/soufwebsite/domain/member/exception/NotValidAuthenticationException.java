package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_VALID_AUTHENTICATION;

public class NotValidAuthenticationException extends BaseErrorException {
    public NotValidAuthenticationException() {
        super(NOT_VALID_AUTHENTICATION.getCode(), NOT_VALID_AUTHENTICATION.getMessage(), NOT_VALID_AUTHENTICATION.getErrorKey());
    }
}
