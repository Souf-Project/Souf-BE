package com.souf.soufwebsite.domain.socialAccount.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.socialAccount.exception.ErrorType.ALREADY_LINKED;

public class AlreadyLinkedException extends BaseErrorException {
    public AlreadyLinkedException(){super(ALREADY_LINKED.getCode(), ALREADY_LINKED.getMessage(), ALREADY_LINKED.getErrorKey());}
}