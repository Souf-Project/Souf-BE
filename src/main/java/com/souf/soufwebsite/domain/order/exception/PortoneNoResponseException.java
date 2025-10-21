package com.souf.soufwebsite.domain.order.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.order.exception.ErrorType.PORTONE_NO_RESPONSE;

public class PortoneNoResponseException extends BaseErrorException {
    public PortoneNoResponseException() {
        super(PORTONE_NO_RESPONSE.getCode(), PORTONE_NO_RESPONSE.getMessage(), PORTONE_NO_RESPONSE.getErrorKey());
    }
}
