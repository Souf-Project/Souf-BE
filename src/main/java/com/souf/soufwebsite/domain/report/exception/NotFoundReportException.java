package com.souf.soufwebsite.domain.report.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.report.exception.ErrorType.NOT_FOUND_REPORT;

public class NotFoundReportException extends BaseErrorException {
    public NotFoundReportException() {
        super(NOT_FOUND_REPORT.getCode(), NOT_FOUND_REPORT.getMessage());
    }
}
