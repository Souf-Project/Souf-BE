package com.souf.soufwebsite.domain.file.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.file.exception.ErrorType.NOT_VALID_FILE_TYPE;

public class NotValidFileTypeException extends BaseErrorException {
    public NotValidFileTypeException() {
        super(NOT_VALID_FILE_TYPE.getCode(), NOT_VALID_FILE_TYPE.getMessage(), NOT_VALID_FILE_TYPE.getErrorKey());
    }
}
