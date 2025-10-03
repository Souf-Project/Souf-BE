package com.souf.soufwebsite.domain.inquiry.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.inquiry.exception.ErrorType.NOT_FOUND_INQUIRY;

public class NotFoundInquiryException extends BaseErrorException {
    public NotFoundInquiryException() {
        super(NOT_FOUND_INQUIRY.getCode(), NOT_FOUND_INQUIRY.getMessage(), NOT_FOUND_INQUIRY.getErrorKey());
    }
}
