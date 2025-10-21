package com.souf.soufwebsite.domain.order.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.order.exception.ErrorType.MISMATCH_AMOUNT;

public class MismatchAmountException extends BaseErrorException {
    public MismatchAmountException() {
        super(MISMATCH_AMOUNT.getCode(), MISMATCH_AMOUNT.getMessage(), MISMATCH_AMOUNT.getErrorKey());
    }
}
