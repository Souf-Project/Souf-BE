package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_VALID_EMAIL;

public class NotValidEmailException extends BaseErrorException {
    public NotValidEmailException() { super(NOT_VALID_EMAIL.getCode(), NOT_VALID_EMAIL.getMessage()); }
}
