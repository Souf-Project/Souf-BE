package com.souf.soufwebsite.domain.application.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.application.exception.ErrorType.NOT_FOUND_RECRUIT;

public class NotFoundRecruitException extends BaseErrorException {
    public NotFoundRecruitException() {
        super(NOT_FOUND_RECRUIT.getCode(), NOT_FOUND_RECRUIT.getMessage());
    }
}
