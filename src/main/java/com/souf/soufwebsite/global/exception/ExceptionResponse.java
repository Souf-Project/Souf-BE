package com.souf.soufwebsite.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ExceptionResponse<T> {

    private int code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String errorKey;
    private T data;

    public static <T> ExceptionResponse<T> fail(int code, String message) {
        return fail(code, message, null);
    }

    public static <T> ExceptionResponse<T> fail(int code, String message, String errorKey) {
        return ExceptionResponse.<T>builder()
                .code(code)
                .message(message)
                .errorKey(errorKey)
                .build();
    }
}
