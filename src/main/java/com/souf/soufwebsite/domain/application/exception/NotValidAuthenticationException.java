package com.souf.soufwebsite.domain.application.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.feed.exception.ErrorType.NOT_VALID_AUTHENTICATION;

public class NotValidAuthenticationException extends BaseErrorException {
    public NotValidAuthenticationException() {
        super(NOT_VALID_AUTHENTICATION.getCode(), NOT_VALID_AUTHENTICATION.getMessage());
    }
}
