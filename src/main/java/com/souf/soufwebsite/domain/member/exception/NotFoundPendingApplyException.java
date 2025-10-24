package com.souf.soufwebsite.domain.member.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.member.exception.ErrorType.NOT_FOUND_PENDING_APPLY;

public class NotFoundPendingApplyException extends BaseErrorException {

    public NotFoundPendingApplyException() {
        super(NOT_FOUND_PENDING_APPLY.getCode(), NOT_FOUND_PENDING_APPLY.getMessage(), NOT_FOUND_PENDING_APPLY.getErrorKey());
    }
}
