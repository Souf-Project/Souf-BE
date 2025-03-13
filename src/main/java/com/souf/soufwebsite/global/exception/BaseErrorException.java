package com.souf.soufwebsite.global.exception;

import lombok.Getter;

@Getter
public class BaseErrorException extends RuntimeException{

    private final int errorCode;

    public BaseErrorException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }
}
