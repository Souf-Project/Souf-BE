package com.souf.soufwebsite.domain.notification.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.notification.exception.ErrorType.NOT_FOUND_NOTIFICATION;

public class NotFoundNotificationException extends BaseErrorException {
    public NotFoundNotificationException() {
        super(NOT_FOUND_NOTIFICATION.getCode(), NOT_FOUND_NOTIFICATION.getMessage(), NOT_FOUND_NOTIFICATION.getErrorKey());
    }
}
