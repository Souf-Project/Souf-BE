package com.souf.soufwebsite.domain.comment.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.comment.exception.ErrorType.NOT_MATCHED_OWNER;

public class NotMatchedOwnerException extends BaseErrorException {
    public NotMatchedOwnerException() {
        super(NOT_MATCHED_OWNER.getCode(), NOT_MATCHED_OWNER.getMessage(), NOT_MATCHED_OWNER.getErrorKey());
    }
}
