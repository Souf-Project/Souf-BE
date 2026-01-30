package com.souf.soufwebsite.domain.comment.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.comment.exception.ErrorType.NOT_MATCHED_COMMENT_AND_FEED;

public class NotMatchedCommentAndFeedException extends BaseErrorException {
    public NotMatchedCommentAndFeedException() {
        super(NOT_MATCHED_COMMENT_AND_FEED.getCode(), NOT_MATCHED_COMMENT_AND_FEED.getMessage(), NOT_MATCHED_COMMENT_AND_FEED.getErrorKey());
    }
}
