package com.souf.soufwebsite.domain.comment.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.comment.exception.ErrorType.NOT_REPLY_TO_REPLY;

public class NotReplyToReplyException extends BaseErrorException {
    public NotReplyToReplyException() {
        super(
                NOT_REPLY_TO_REPLY.getCode(), NOT_REPLY_TO_REPLY.getMessage(), NOT_REPLY_TO_REPLY.getErrorKey()
        );
    }
}
