package com.souf.soufwebsite.global.exception;

import lombok.Getter;

@Getter
public class BaseErrorException extends RuntimeException{

    private final int code;
    private final String errorKey;

    public BaseErrorException(int code, String errorMessage, String errorKey) {
        super(errorMessage);
        this.code = code;
        this.errorKey = errorKey;
    }
}
