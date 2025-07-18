package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.*;

public class NotFoundSecondCategoryException extends BaseErrorException {

    public NotFoundSecondCategoryException() {
        super(NOT_FOUND_SECOND_CATEGORY.getCode(), NOT_FOUND_SECOND_CATEGORY.getMessage());
    }
}