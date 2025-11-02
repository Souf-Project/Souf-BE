package com.souf.soufwebsite.domain.file.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.file.exception.ErrorType.NOT_FOUND_MEDIA;

public class NotFoundMediaException extends BaseErrorException {
    public NotFoundMediaException() {
        super(NOT_FOUND_MEDIA.getCode(), NOT_FOUND_MEDIA.getMessage(), NOT_FOUND_MEDIA.getErrorKey());
    }
}
