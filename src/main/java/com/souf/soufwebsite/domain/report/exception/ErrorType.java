package com.souf.soufwebsite.domain.report.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_MATCHED_REPORT_OWNER(400, "신고자와 현재 사용자가 일치하지 않습니다.", "R400-1"),
    NOT_FOUND_REPORT(404, "해당 신고 내역이 존재하지 않습니다.", "R404-1"),

    /* ============================== 신고 사유 ===================================== */

    NOT_FOUND_REPORT_REASON(404, "해당 신고 사유가 존재하지 않습니다.", "R404-2"),

    /* ============================= 재제 예외 -------------------------------------- */

    DUPLICATE_SANCTION(400, "동일한 내역이 이미 존재합니다.", "R400-2"),
    DECLARED_MEMBER(401, "이미 신고된 회원입니다.", "R401-1");

    private final int code;
    private final String message;
    private final String errorKey;
}
