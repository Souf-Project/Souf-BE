package com.souf.soufwebsite.domain.chat.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class NotMyApplicantException extends BaseErrorException {
    public NotMyApplicantException() {
        super(ErrorType.NOT_MY_APPLICANT.getCode(), ErrorType.NOT_MY_APPLICANT.getMessage(), ErrorType.NOT_MY_APPLICANT.getErrorKey());
    }
}
