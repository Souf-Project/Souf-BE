package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_AGREED_PERSONAL_INFO;

public class NotAgreedPersonalInfoException extends BaseErrorException {
    public NotAgreedPersonalInfoException() {
        super(NOT_AGREED_PERSONAL_INFO.getCode(), NOT_AGREED_PERSONAL_INFO.getMessage(), NOT_AGREED_PERSONAL_INFO.getErrorKey());
    }
}
