package com.souf.soufwebsite.domain.report.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.report.exception.ErrorType.DECLARED_MEMBER;

public class DeclaredMemberException extends BaseErrorException {
    public DeclaredMemberException(String msg) {
        super(DECLARED_MEMBER.getCode(), msg, DECLARED_MEMBER.getErrorKey());
    }
}
