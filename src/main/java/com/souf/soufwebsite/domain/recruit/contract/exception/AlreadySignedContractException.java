package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.ALREADY_SIGNED_CONTRACT;

public class AlreadySignedContractException extends BaseErrorException {
    public AlreadySignedContractException() {
        super(ALREADY_SIGNED_CONTRACT.getCode(), ALREADY_SIGNED_CONTRACT.getMessage(), ALREADY_SIGNED_CONTRACT.getErrorKey());
    }
}
