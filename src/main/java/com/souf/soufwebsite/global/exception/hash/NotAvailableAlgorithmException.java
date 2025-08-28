package com.souf.soufwebsite.global.exception.hash;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.exception.ErrorType._NOT_AVAILABLE_ALGORITHM;


public class NotAvailableAlgorithmException extends BaseErrorException {
    public NotAvailableAlgorithmException() {
        super(_NOT_AVAILABLE_ALGORITHM.getCode(), _NOT_AVAILABLE_ALGORITHM.getMessage());
    }
}
