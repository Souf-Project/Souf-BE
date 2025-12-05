package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.NOT_ACCEPTED_CONTRACT;

public class NotAcceptedContractException extends BaseErrorException {
    public NotAcceptedContractException() {
        super(NOT_ACCEPTED_CONTRACT.getCode(), NOT_ACCEPTED_CONTRACT.getMessage(), NOT_ACCEPTED_CONTRACT.getErrorKey());
    }
}
