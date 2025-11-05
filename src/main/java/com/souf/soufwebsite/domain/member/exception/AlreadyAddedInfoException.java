package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.ALREADY_ADDED_INFO;

public class AlreadyAddedInfoException extends BaseErrorException {
    public AlreadyAddedInfoException() {
        super(ALREADY_ADDED_INFO.getCode(), ALREADY_ADDED_INFO.getMessage(), ALREADY_ADDED_INFO.getErrorKey());
    }
}
