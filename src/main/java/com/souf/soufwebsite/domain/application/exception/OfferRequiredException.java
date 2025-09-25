package com.souf.soufwebsite.domain.application.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.application.exception.ErrorType.OFFER_REQUIRED;

public class OfferRequiredException extends BaseErrorException {
    public OfferRequiredException() {
        super(OFFER_REQUIRED.getCode(), OFFER_REQUIRED.getMessage(), OFFER_REQUIRED.getErrorKey());
    }
}
