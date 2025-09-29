package com.souf.soufwebsite.domain.review.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.review.exception.ErrorType.NOT_FOUND_REVIEW;

public class NotFoundReviewException extends BaseErrorException {
    public NotFoundReviewException() {
        super(NOT_FOUND_REVIEW.getCode(), NOT_FOUND_REVIEW.getMessage(), NOT_FOUND_REVIEW.getErrorKey());
    }
}
