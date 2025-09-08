package com.souf.soufwebsite.domain.report.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.report.exception.ErrorType.DUPLICATE_SANCTION;

public class DuplicateSanctionException extends BaseErrorException {
    public DuplicateSanctionException() {
        super(DUPLICATE_SANCTION.getCode(), DUPLICATE_SANCTION.getMessage(), DUPLICATE_SANCTION.getErrorKey());
    }
}
