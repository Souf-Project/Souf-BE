package com.souf.soufwebsite.domain.application.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_RECRUITABLE(400, "해당 공고는 지원이 종료되었습니다.", "A400-1"),
    ALREADY_APPLIED(409, "이미 지원한 공고입니다.", "A409-1"),
    NOT_FOUND_APPLICATION(404, "해당 지원서를 찾을 수 없습니다.", "A404-1"),
    NOT_VALID_AUTHENTICATION(403, "해당 지원을 관리할 권한이 없습니다.", "A403-1"),
    NOT_APPLY_MY_RECRUIT(400, "자신의 공고에는 지원할 수 없습니다.", "A400-2"),
    OFFER_REQUIRED(400, "PricePolicy가 OFFER일 때, 가격과 견적 사유는 필수입니다.", "A400-3");

    private final int code;
    private final String message;
    private final String errorKey;

}