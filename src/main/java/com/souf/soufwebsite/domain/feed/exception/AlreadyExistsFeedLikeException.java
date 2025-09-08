package com.souf.soufwebsite.domain.feed.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.feed.exception.ErrorType.ALREADY_EXISTS_LIKE;

public class AlreadyExistsFeedLikeException extends BaseErrorException {
    public AlreadyExistsFeedLikeException() {
        super(ALREADY_EXISTS_LIKE.getCode(), ALREADY_EXISTS_LIKE.getMessage(), ALREADY_EXISTS_LIKE.getErrorKey());
    }
}
