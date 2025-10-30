package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_VALID_ROLE_TYPE;

public class NotValidRoleTypeException extends BaseErrorException {
    public NotValidRoleTypeException() {
        super(NOT_VALID_ROLE_TYPE.getCode(), NOT_VALID_ROLE_TYPE.getMessage(), NOT_VALID_ROLE_TYPE.getErrorKey());
    }
}
