package com.souf.soufwebsite.global.exception.hash;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.exception.ErrorType.NOT_AVAILABLE_ALGORITHM;


public class NotAvailableAlgorithmException extends BaseErrorException {
    public NotAvailableAlgorithmException() {
        super(NOT_AVAILABLE_ALGORITHM.getCode(), NOT_AVAILABLE_ALGORITHM.getMessage(), NOT_AVAILABLE_ALGORITHM.getErrorKey());
    }
}
