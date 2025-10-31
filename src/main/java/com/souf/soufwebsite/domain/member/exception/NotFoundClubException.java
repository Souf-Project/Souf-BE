package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_FOUND_CLUB;

public class NotFoundClubException extends BaseErrorException {

    public NotFoundClubException() {
        super(NOT_FOUND_CLUB.getCode(), NOT_FOUND_CLUB.getMessage(), NOT_FOUND_CLUB.getErrorKey());
    }
}
