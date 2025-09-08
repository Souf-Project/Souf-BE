package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType.NOT_FOUND_RECRUIT;


public class NotFoundRecruitException extends BaseErrorException {
    public NotFoundRecruitException() {
        super(NOT_FOUND_RECRUIT.getCode(), NOT_FOUND_RECRUIT.getMessage(), NOT_FOUND_RECRUIT.getErrorKey());
    }
}
