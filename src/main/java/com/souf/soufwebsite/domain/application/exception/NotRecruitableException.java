package com.souf.soufwebsite.domain.application.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.application.exception.ErrorType.NOT_RECRUITABLE;

public class NotRecruitableException extends BaseErrorException {
    public NotRecruitableException() {
        super(NOT_RECRUITABLE.getCode(), NOT_RECRUITABLE.getMessage());
    }
}