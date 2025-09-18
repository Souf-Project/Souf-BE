package com.souf.soufwebsite.domain.city.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.city.exception.ErrorType.REQUIRED_CITY_DETAIL;

public class RequiredCityDetailException extends BaseErrorException {
    public RequiredCityDetailException() {
        super(REQUIRED_CITY_DETAIL.getCode(), REQUIRED_CITY_DETAIL.getMessage(), REQUIRED_CITY_DETAIL.getErrorKey());
    }
}
