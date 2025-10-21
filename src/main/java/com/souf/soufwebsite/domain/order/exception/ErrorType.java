package com.souf.soufwebsite.domain.order.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_ORDER(404, "주문 내역을 찾을 수 없습니다.", "O404-1"),
    PORTONE_NO_RESPONSE(400, "포트원이 응답하지 않습니다.", "O400-1"),
    MISMATCH_AMOUNT(406, "금액이 일치하지 않습니다.", "O406-1"),
    NOT_PAID(406, "금액이 지불된 상태가 아닙니다.", "O406-2");

    private final int code;
    private final String message;
    private final String errorKey;
}
