package com.souf.soufwebsite.domain.feed.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.feed.exception.ErrorType.NOT_FOUND_FEED;

public class NotFoundFeedException extends BaseErrorException {
    public NotFoundFeedException() {
        super(NOT_FOUND_FEED.getCode(), NOT_FOUND_FEED.getMessage());
    }
}
