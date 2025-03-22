package com.souf.soufwebsite.domain.feed.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class NotValidAuthenticationException extends BaseErrorException {
    public NotValidAuthenticationException() {
        super(ErrorType._NOT_VALID_AUTHENTICATION.getCode(), ErrorType._NOT_VALID_AUTHENTICATION.getMessage());
    }
}
