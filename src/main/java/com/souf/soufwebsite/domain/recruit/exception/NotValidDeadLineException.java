package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType.NOT_VALID_DEADLINE;

public class NotValidDeadLineException extends BaseErrorException {
    public NotValidDeadLineException() {
        super(NOT_VALID_DEADLINE.getCode(), NOT_VALID_DEADLINE.getMessage(), NOT_VALID_DEADLINE.getErrorKey());
    }
}
