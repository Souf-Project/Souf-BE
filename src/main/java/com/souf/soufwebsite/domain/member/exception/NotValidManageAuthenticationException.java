package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_VALID_MANAGE_AUTHENTICATION;

public class NotValidManageAuthenticationException extends BaseErrorException {
    public NotValidManageAuthenticationException() {
        super(NOT_VALID_MANAGE_AUTHENTICATION.getCode(), NOT_VALID_MANAGE_AUTHENTICATION.getMessage(), NOT_VALID_MANAGE_AUTHENTICATION.getErrorKey());
    }
}
