package com.souf.soufwebsite.domain.file.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.file.exception.ErrorType.NOT_REQUESTED_FILE;

public class NotRequestedFileException extends BaseErrorException {
    public NotRequestedFileException() {
        super(NOT_REQUESTED_FILE.getCode(), NOT_REQUESTED_FILE.getMessage(), NOT_REQUESTED_FILE.getErrorKey());
    }
}
