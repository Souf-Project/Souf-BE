package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType._NOT_VALID_AUTHENTICATION;

public class NotValidAuthenticationException extends BaseErrorException {
    public NotValidAuthenticationException() {
        super(_NOT_VALID_AUTHENTICATION.getCode(), _NOT_VALID_AUTHENTICATION.getMessage());
    }
}
