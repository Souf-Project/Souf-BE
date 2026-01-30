package com.souf.soufwebsite.domain.comment.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.comment.exception.ErrorType.NOT_EXISTS_DELETED_COMMENT;

public class NotExistsDeletedComment extends BaseErrorException {
    public NotExistsDeletedComment() {
        super(NOT_EXISTS_DELETED_COMMENT.getCode(), NOT_EXISTS_DELETED_COMMENT.getMessage(), NOT_EXISTS_DELETED_COMMENT.getErrorKey());
    }
}
