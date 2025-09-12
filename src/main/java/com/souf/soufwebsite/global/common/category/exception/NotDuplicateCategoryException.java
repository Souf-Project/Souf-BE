package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_DUPLICATE_CATEGORY;

public class NotDuplicateCategoryException extends BaseErrorException {
    public NotDuplicateCategoryException() {
        super(NOT_DUPLICATE_CATEGORY.getCode(), NOT_DUPLICATE_CATEGORY.getMessage(), NOT_DUPLICATE_CATEGORY.getErrorKey());
    }
}
