package com.souf.soufwebsite.domain.city.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.city.exception.ErrorType.NOT_MATCHED_CITY_DETAIL;

public class NotMatchedCityDetailException extends BaseErrorException {
    public NotMatchedCityDetailException() {
        super(NOT_MATCHED_CITY_DETAIL.getCode(), NOT_MATCHED_CITY_DETAIL.getMessage(), NOT_MATCHED_CITY_DETAIL.getErrorKey());
    }
}
