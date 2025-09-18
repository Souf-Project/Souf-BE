package com.souf.soufwebsite.domain.city.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_FOUND_CITY(404, "유효하지 않은 도시입니다.", "C404-1"),
    NOT_FOUND_CITY_DETAIL(404, "유효하지 않은 지역입니다.", "C404-2"),
    REQUIRED_CITY_DETAIL(400, "상세지역은 필수입니다.", "C400-1"),;


    private final int code;
    private final String message;
    private final String errorKey;
}
