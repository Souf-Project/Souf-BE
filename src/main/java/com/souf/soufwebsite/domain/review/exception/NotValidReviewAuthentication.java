package com.souf.soufwebsite.domain.review.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.review.exception.ErrorType.NOT_VALID_AUTHENTICATION;

public class NotValidReviewAuthentication extends BaseErrorException {
    public NotValidReviewAuthentication() {
        super(NOT_VALID_AUTHENTICATION.getCode(), NOT_VALID_AUTHENTICATION.getMessage(), NOT_VALID_AUTHENTICATION.getErrorKey());
    }
}
