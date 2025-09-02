package com.souf.soufwebsite.domain.socialAccount.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.socialAccount.exception.ErrorType.DUPLICATE_EMAIL;

public class DuplicateEmailException extends BaseErrorException {
    public DuplicateEmailException(){super(DUPLICATE_EMAIL.getCode(), DUPLICATE_EMAIL.getMessage());}
}
