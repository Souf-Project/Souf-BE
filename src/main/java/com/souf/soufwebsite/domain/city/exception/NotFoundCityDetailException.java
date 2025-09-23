package com.souf.soufwebsite.domain.city.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.city.exception.ErrorType.NOT_FOUND_CITY_DETAIL;

public class NotFoundCityDetailException extends BaseErrorException {
    public NotFoundCityDetailException() {
        super(NOT_FOUND_CITY_DETAIL.getCode(), NOT_FOUND_CITY_DETAIL.getMessage(), NOT_FOUND_CITY_DETAIL.getErrorKey());
    }
}
