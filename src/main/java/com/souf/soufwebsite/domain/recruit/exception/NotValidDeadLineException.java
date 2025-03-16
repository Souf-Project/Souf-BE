package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType._NOT_VALID_DEADLINE;

public class NotValidDeadLineException extends BaseErrorException {
    public NotValidDeadLineException() {
        super(_NOT_VALID_DEADLINE.getCode(), _NOT_VALID_DEADLINE.getMessage());
    }
}
