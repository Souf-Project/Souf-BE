package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.ALREADY_JOINED_CLUB;

public class AlreadyJoinedClubException extends BaseErrorException {
    public AlreadyJoinedClubException() {
        super(ALREADY_JOINED_CLUB.getCode(), ALREADY_JOINED_CLUB.getMessage(), ALREADY_JOINED_CLUB.getErrorKey());
    }
}
