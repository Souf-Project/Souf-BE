package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType.NOT_BLANK_PRICE;

public class NotBlankPriceException extends BaseErrorException {
    public NotBlankPriceException() {
        super(NOT_BLANK_PRICE.getCode(), NOT_BLANK_PRICE.getMessage(), NOT_BLANK_PRICE.getErrorKey());
    }
}
