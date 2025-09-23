package com.souf.soufwebsite.domain.socialAccount.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.socialAccount.exception.ErrorType.NOT_VALID_TOKEN;

public class NotValidTokenException extends BaseErrorException {
    public NotValidTokenException() {
        super(NOT_VALID_TOKEN.getCode(), NOT_VALID_TOKEN.getMessage(), NOT_VALID_TOKEN.getErrorKey());
    }
}
