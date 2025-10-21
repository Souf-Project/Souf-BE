package com.souf.soufwebsite.domain.order.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.order.exception.ErrorType.NOT_PAID;

public class NotPaidException extends BaseErrorException {
    public NotPaidException() {
        super(NOT_PAID.getCode(), NOT_PAID.getMessage(), NOT_PAID.getErrorKey());
    }
}
