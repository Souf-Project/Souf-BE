package com.souf.soufwebsite.global.exception;

import static com.souf.soufwebsite.global.exception.ErrorType.NOT_AUTHORIZED;

public class AuthorizedException extends BaseErrorException {
    public AuthorizedException() {
        super(NOT_AUTHORIZED.getCode(), NOT_AUTHORIZED.getMessage(), NOT_AUTHORIZED.getErrorKey());
    }
}