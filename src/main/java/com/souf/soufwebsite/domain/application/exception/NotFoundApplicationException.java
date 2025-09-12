package com.souf.soufwebsite.domain.application.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.application.exception.ErrorType.NOT_FOUND_APPLICATION;

public class NotFoundApplicationException extends BaseErrorException {
    public NotFoundApplicationException() {
        super(NOT_FOUND_APPLICATION.getCode(), NOT_FOUND_APPLICATION.getMessage(), NOT_FOUND_APPLICATION.getErrorKey());
    }
}
