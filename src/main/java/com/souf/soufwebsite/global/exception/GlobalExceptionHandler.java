package com.souf.soufwebsite.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";
    private static final int BAD_REQUEST = 400;
    private static final int SERVER_ERROR_CODE = 500;

    @ExceptionHandler(BaseErrorException.class)
    public ResponseEntity<ExceptionResponse<Void>> handle(BaseErrorException e) {
        logByStatus(e, e.getCode());
        ExceptionResponse<Void> response = ExceptionResponse.fail(e.getCode(), e.getMessage(), e.getErrorCode());

        return ResponseEntity
                .status(e.getCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    // @Valid 예외 처리 (@NotNull, @Size, etc...) or IllegalArgumentException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<Void>> handle(MethodArgumentNotValidException e) {

        log.warn("Bad Request: {}", e.getMessage(), e);

        ExceptionResponse<Void> response = ExceptionResponse.fail(BAD_REQUEST, "잘못된 요청입니다.");


        return ResponseEntity
                .status(BAD_REQUEST)
                .body(response);
    }

    // 서버 측 에러 (이외의 에러)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<Void>> handle(Exception e) {

        log.error("Unhandled exception: {}", e.getMessage(), e);
        ExceptionResponse<Void> response = ExceptionResponse.fail(SERVER_ERROR_CODE, "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요");

        return ResponseEntity
                .status(SERVER_ERROR_CODE)
                .body(response);
    }

    private void logByStatus(Exception e, int status) {
        if (status >= 500) log.error("Class: {}, Code: {}, Msg: {}", e.getClass().getSimpleName(), status, e.getMessage(), e);
        else log.warn("Class: {}, Code: {}, Msg: {}", e.getClass().getSimpleName(), status, e.getMessage());
    }
}
