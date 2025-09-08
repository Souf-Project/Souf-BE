package com.souf.soufwebsite.domain.socialAccount.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.socialAccount.exception.ErrorType.ALREADY_LINKED_OTHER_USER;

public class AlreadyLinkedOtherUserException extends BaseErrorException {
    public AlreadyLinkedOtherUserException(){
        super(ALREADY_LINKED_OTHER_USER.getCode(), ALREADY_LINKED_OTHER_USER.getMessage(), ALREADY_LINKED_OTHER_USER.getErrorKey());}
}
