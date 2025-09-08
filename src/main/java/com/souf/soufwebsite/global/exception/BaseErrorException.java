package com.souf.soufwebsite.global.exception;

import lombok.Getter;

@Getter
public class BaseErrorException extends RuntimeException{

    private final int code;
    private final String errorCode;

    public BaseErrorException(int code, String errorMessage, String errorCode) {
        super(errorMessage);
        this.code = code;
        this.errorCode = errorCode;
    }
}
