package com.souf.soufwebsite.domain.feed.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.feed.exception.ErrorType._NOT_FOUND_FEED;

public class NotFoundFeedException extends BaseErrorException {
    public NotFoundFeedException() {
        super(_NOT_FOUND_FEED.getCode(), _NOT_FOUND_FEED.getMessage());
    }
}
