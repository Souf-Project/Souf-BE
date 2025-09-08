package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_FOUND_FAVORITE;

public class NotFoundFavoriteException extends BaseErrorException {
    public NotFoundFavoriteException() {
        super(NOT_FOUND_FAVORITE.getCode(), NOT_FOUND_FAVORITE.getMessage(), NOT_FOUND_FAVORITE.getErrorKey());
    }
}
