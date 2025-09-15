package com.souf.soufwebsite.domain.report.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.report.exception.ErrorType.NOT_FOUND_REPORT_REASON;

public class NotFoundReportReasonException extends BaseErrorException {
    public NotFoundReportReasonException() {
        super(NOT_FOUND_REPORT_REASON.getCode(), NOT_FOUND_REPORT_REASON.getMessage(), NOT_FOUND_REPORT_REASON.getErrorKey());
    }
}
