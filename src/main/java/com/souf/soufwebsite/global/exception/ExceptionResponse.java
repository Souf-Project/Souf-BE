package com.souf.soufwebsite.global.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ExceptionResponse<T> {

    private int code;
    private String message;
    private T data;

    public ExceptionResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    private ExceptionResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static <T> ExceptionResponse<T> fail(int code, String message) {
        return new ExceptionResponse<>(code, message);
    }
}
