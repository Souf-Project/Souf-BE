package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_ALLOW_SIGNUP;

public class NotAllowedSignupException extends BaseErrorException {
    public NotAllowedSignupException() {
        super(NOT_ALLOW_SIGNUP.getCode(), NOT_ALLOW_SIGNUP.getMessage());
    }
}
