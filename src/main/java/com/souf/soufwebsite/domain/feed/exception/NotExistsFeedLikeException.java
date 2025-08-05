package com.souf.soufwebsite.domain.feed.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.feed.exception.ErrorType.NOT_EXISTS_LIKE;

public class NotExistsFeedLikeException extends BaseErrorException {
    public NotExistsFeedLikeException() {
        super(NOT_EXISTS_LIKE.getCode(), NOT_EXISTS_LIKE.getMessage());
    }
}
