package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.NOT_FOUND_CONTRACT_INVITE;

public class NotFoundContractInviteException extends BaseErrorException {
    public NotFoundContractInviteException() {
        super(NOT_FOUND_CONTRACT_INVITE.getCode(), NOT_FOUND_CONTRACT_INVITE.getMessage(), NOT_FOUND_CONTRACT_INVITE.getErrorKey());
    }
}
