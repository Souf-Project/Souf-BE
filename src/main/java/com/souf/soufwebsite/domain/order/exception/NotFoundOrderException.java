package com.souf.soufwebsite.domain.order.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.order.exception.ErrorType.NOT_FOUND_ORDER;

public class NotFoundOrderException extends BaseErrorException {
    public NotFoundOrderException() {
        super(NOT_FOUND_ORDER.getCode(), NOT_FOUND_ORDER.getMessage(), NOT_FOUND_ORDER.getErrorKey());
    }
}
