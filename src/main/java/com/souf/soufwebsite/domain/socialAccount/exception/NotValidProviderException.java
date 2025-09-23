package com.souf.soufwebsite.domain.socialAccount.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.socialAccount.exception.ErrorType.NOT_VALID_PROVIDER;

public class NotValidProviderException extends BaseErrorException {
    public NotValidProviderException() {
        super(NOT_VALID_PROVIDER.getCode(), NOT_VALID_PROVIDER.getMessage(), NOT_VALID_PROVIDER.getErrorKey());
    }
}
