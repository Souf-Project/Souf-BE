package com.souf.soufwebsite.global.exception;

import static com.souf.soufwebsite.global.exception.ErrorType._NOT_AUTHORIZED;

public class AuthorizedException extends BaseErrorException {
    public AuthorizedException() {
        super(_NOT_AUTHORIZED.getCode(), _NOT_AUTHORIZED.getMessage());
    }
}