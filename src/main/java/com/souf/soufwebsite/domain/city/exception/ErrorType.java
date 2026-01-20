package com.souf.soufwebsite.domain.city.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_CITY(404, "유효하지 않은 도시입니다.", "CT404-1"),
    NOT_FOUND_CITY_DETAIL(404, "유효하지 않은 지역입니다.", "CT404-2"),

    /* ============================ 400 ================================= */
    REQUIRED_CITY_DETAIL(400, "상세지역은 필수입니다.", "CT400-1"),

    /* ============================ 409 ================================= */
    NOT_MATCHED_CITY_DETAIL(409, "도시와 상세 도시가 일치하지 않습니다.", "CT409-1");


    private final int code;
    private final String message;
    private final String errorKey;
}
