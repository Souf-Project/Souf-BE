package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_APPROVED_ACCOUNT;

public class NotApprovedAccountException extends BaseErrorException {
    public NotApprovedAccountException() {
        super(NOT_APPROVED_ACCOUNT.getCode(), NOT_APPROVED_ACCOUNT.getMessage(), NOT_APPROVED_ACCOUNT.getErrorKey());
    }
}
