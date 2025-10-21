package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_MATCH_ROLE;

public class NotMatchRoleException extends BaseErrorException {
    public NotMatchRoleException() {
        super(NOT_MATCH_ROLE.getCode(), NOT_MATCH_ROLE.getMessage(), NOT_MATCH_ROLE.getErrorKey());
    }
}
