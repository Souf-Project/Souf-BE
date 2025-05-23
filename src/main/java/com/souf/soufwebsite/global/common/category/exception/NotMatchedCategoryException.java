package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_MATCHED_CATEGORY;

public class NotMatchedCategoryException extends BaseErrorException {
    public NotMatchedCategoryException(String message) {

        super(NOT_MATCHED_CATEGORY.getCode(), message);
    }
}
