package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_MATCH_PASSWORD;

public class NotMatchPasswordException extends BaseErrorException {
    public NotMatchPasswordException() {
        super(NOT_MATCH_PASSWORD.getCode(), NOT_MATCH_PASSWORD.getMessage(), NOT_MATCH_PASSWORD.getErrorKey()); }
}