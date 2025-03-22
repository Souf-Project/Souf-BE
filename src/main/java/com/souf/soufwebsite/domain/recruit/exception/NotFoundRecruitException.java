package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType._NOT_FOUND_RECRUIT;

public class NotFoundRecruitException extends BaseErrorException {
    public NotFoundRecruitException() {
        super(_NOT_FOUND_RECRUIT.getCode(), _NOT_FOUND_RECRUIT.getMessage());
    }
}
