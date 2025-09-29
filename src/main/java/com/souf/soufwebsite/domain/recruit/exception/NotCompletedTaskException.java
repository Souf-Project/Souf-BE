package com.souf.soufwebsite.domain.recruit.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.exception.ErrorType.NOT_COMPLETED_TASK;

public class NotCompletedTaskException extends BaseErrorException {
    public NotCompletedTaskException() {
        super(NOT_COMPLETED_TASK.getCode(), NOT_COMPLETED_TASK.getMessage(), NOT_COMPLETED_TASK.getErrorKey());
    }
}
