package com.souf.soufwebsite.domain.feed.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class NotValidAuthenticationException extends BaseErrorException {
    public NotValidAuthenticationException() {
        super(ErrorType.NOT_VALID_AUTHENTICATION.getCode(), ErrorType.NOT_VALID_AUTHENTICATION.getMessage());
    }
}
