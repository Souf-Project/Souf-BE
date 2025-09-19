package com.souf.soufwebsite.domain.city.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.domain.city.exception.ErrorType.NOT_FOUND_CITY;

public class NotFoundCityException extends BaseErrorException {
    public NotFoundCityException() {
        super(NOT_FOUND_CITY.getCode(), NOT_FOUND_CITY.getMessage(), NOT_FOUND_CITY.getErrorKey());
    }
}
