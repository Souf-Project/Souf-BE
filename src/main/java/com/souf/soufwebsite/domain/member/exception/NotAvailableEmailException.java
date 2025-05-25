package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_AVAILABLE_EMAIL;

public class NotAvailableEmailException extends BaseErrorException {
    public NotAvailableEmailException() { super(NOT_AVAILABLE_EMAIL.getCode(), NOT_AVAILABLE_EMAIL.getMessage()); }
}
