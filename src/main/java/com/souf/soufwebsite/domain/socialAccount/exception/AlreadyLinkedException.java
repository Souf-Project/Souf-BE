package com.souf.soufwebsite.domain.socialAccount.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class AlreadyLinkedException extends BaseErrorException {
    public AlreadyLinkedException(){super(ErrorType.ALREADY_LINKED.getCode(), ErrorType.ALREADY_LINKED.getMessage());}
}