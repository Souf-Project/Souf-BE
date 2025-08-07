package com.souf.soufwebsite.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";
    private static final int ERROR_CODE = 400;
    private static final int SERVER_ERROR_CODE = 500;

    @ExceptionHandler(BaseErrorException.class)
    public ResponseEntity<ExceptionResponse<Void>> handle(BaseErrorException e) {
        logWarning(e, e.getErrorCode());
        ExceptionResponse<Void> response = ExceptionResponse.fail(e.getErrorCode(), e.getMessage());

        return ResponseEntity
                .status(e.getErrorCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    // @Valid 예외 처리 (@NotNull, @Size, etc...) or IllegalArgumentException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<Void>> handle(MethodArgumentNotValidException e) {

        logWarning(e, ERROR_CODE);
        ExceptionResponse<Void> response = ExceptionResponse.fail(ERROR_CODE, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());

        return ResponseEntity
                .status(ERROR_CODE)
                .body(response);
    }

    // 서버 측 에러 (이외의 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<Void>> handle(Exception e) {

        logWarning(e, SERVER_ERROR_CODE);
        ExceptionResponse<Void> response = ExceptionResponse.fail(SERVER_ERROR_CODE, e.getMessage());

        return ResponseEntity
                .status(SERVER_ERROR_CODE)
                .body(response);
    }

    private void logWarning(Exception e, int errorCode) {
        log.warn(e.getMessage(), e);
        log.warn(LOG_FORMAT, e.getClass().getSimpleName(), errorCode, e.getMessage());
    }
}
