package com.souf.soufwebsite.domain.recruit.contract.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.recruit.contract.exception.ErrorType.ALREADY_EXISTS_PROGRESSING_CONTRACT;

public class AlreadyExistsProgressingContractException extends BaseErrorException {
    public AlreadyExistsProgressingContractException() {
        super(ALREADY_EXISTS_PROGRESSING_CONTRACT.getCode(), ALREADY_EXISTS_PROGRESSING_CONTRACT.getMessage(), ALREADY_EXISTS_PROGRESSING_CONTRACT.getErrorKey());
    }
}
