package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_VALID_EMAIL_PASSWORD;

public class NotValidEmailPasswordException extends BaseErrorException {
    public NotValidEmailPasswordException() {
        super(NOT_VALID_EMAIL_PASSWORD.getCode(), NOT_VALID_EMAIL_PASSWORD.getMessage(), NOT_VALID_EMAIL_PASSWORD.getErrorKey());
    }
}
