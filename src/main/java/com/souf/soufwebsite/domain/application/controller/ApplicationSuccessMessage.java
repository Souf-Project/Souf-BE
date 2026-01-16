package com.souf.soufwebsite.domain.application.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicationSuccessMessage {

    /* ============================== STUDENT ======================== */
    APPLY_SUCCESS("지원이 완료되었습니다."),
    APPLY_CANCELED("지원이 취소되었습니다."),
    MY_APPLICATION_READ_SUCCESS("내 지원서를 조회하였습니다."),

    /* ============================== RECRUIT ======================== */
    APPLICATION_DECISION_SUCCESS("지원서 심사 결과를 반영하였습니다."),
    APPLICATION_READ_SUCCESS("지원서를 조회하였습니다.");


    private final String message;
}
