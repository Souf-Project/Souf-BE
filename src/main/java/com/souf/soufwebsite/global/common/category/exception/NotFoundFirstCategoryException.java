package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_FOUND_FIRST_CATEGORY;

public class NotFoundFirstCategoryException extends BaseErrorException {

    public NotFoundFirstCategoryException() {
        super(NOT_FOUND_FIRST_CATEGORY.getCode(), NOT_FOUND_FIRST_CATEGORY.getMessage(), NOT_FOUND_FIRST_CATEGORY.getErrorKey());
    }
}