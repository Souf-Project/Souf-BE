package com.souf.soufwebsite.domain.socialAccount.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

public class AlreadyLinkedOtherUserException extends BaseErrorException {
    public AlreadyLinkedOtherUserException(){super(ErrorType.ALREADY_LINKED_OTHER_USER.getCode(), ErrorType.ALREADY_LINKED_OTHER_USER.getMessage());}
}
