package com.souf.soufwebsite.domain.report.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.report.exception.ErrorType.NOT_MATCHED_REPORT_OWNER;

public class NotMatchedReportOwnerException extends BaseErrorException {
    public NotMatchedReportOwnerException() {
        super(NOT_MATCHED_REPORT_OWNER.getCode(), NOT_MATCHED_REPORT_OWNER.getMessage(), NOT_MATCHED_REPORT_OWNER.getErrorKey());
    }
}
