package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_FOUND_THIRD_CATEGORY;

public class NotFoundThirdCategoryException extends BaseErrorException {

    public NotFoundThirdCategoryException() {
        super(NOT_FOUND_THIRD_CATEGORY.getCode(), NOT_FOUND_THIRD_CATEGORY.getMessage(), NOT_FOUND_THIRD_CATEGORY.getErrorKey());
    }
}