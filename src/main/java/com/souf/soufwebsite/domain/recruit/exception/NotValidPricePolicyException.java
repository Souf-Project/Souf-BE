package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType.NOT_VALID_PRICE_POLICY;

public class NotValidPricePolicyException extends BaseErrorException {

    public NotValidPricePolicyException() {
            super(NOT_VALID_PRICE_POLICY.getCode(), NOT_VALID_PRICE_POLICY.getMessage(), NOT_VALID_PRICE_POLICY.getErrorKey());
        }
}
