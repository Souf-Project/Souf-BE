package com.souf.soufwebsite.domain.report.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportSuccessMessage {

    REPORT_CREATE("성공적으로 신고되었습니다.");

    private final String message;
}
