package com.souf.soufwebsite.domain.comment.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.comment.exception.ErrorType.NOT_FOUND_COMMENT;

public class NotFoundCommentException extends BaseErrorException {
    public NotFoundCommentException() {
        super(NOT_FOUND_COMMENT.getCode(), NOT_FOUND_COMMENT.getMessage());
    }
}
