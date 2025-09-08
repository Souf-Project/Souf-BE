package com.souf.soufwebsite.domain.application.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.application.exception.ErrorType.ALREADY_APPLIED;

public class AlreadyAppliedException extends BaseErrorException {
    public AlreadyAppliedException() {
        super(ALREADY_APPLIED.getCode(), ALREADY_APPLIED.getMessage(), ALREADY_APPLIED.getErrorKey());
    }
}
