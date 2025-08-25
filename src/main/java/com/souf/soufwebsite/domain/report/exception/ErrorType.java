package com.souf.soufwebsite.domain.report.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_MATCHED_REPORT_OWNER(404, "신고자와 현재 사용자가 일치하지 않습니다."),

    /* ============================== 신고 사유 ===================================== */

    NOT_FOUND_REPORT_REASON(404, "해당 신고 사유가 존재하지 않습니다.");

    private final int code;
    private final String message;
}
