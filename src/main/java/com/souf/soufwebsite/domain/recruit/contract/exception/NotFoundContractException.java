package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.NOT_FOUND_CONTRACT;

public class NotFoundContractException extends BaseErrorException {
    public NotFoundContractException() {
        super(NOT_FOUND_CONTRACT.getCode(), NOT_FOUND_CONTRACT.getMessage(), NOT_FOUND_CONTRACT.getErrorKey());
    }
}
