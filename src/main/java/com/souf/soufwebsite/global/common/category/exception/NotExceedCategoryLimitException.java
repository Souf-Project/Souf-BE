package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_EXCEED_CATEGORY_LIMIT;

public class NotExceedCategoryLimitException extends BaseErrorException {

    public NotExceedCategoryLimitException() {
        super(NOT_EXCEED_CATEGORY_LIMIT.getCode(), NOT_EXCEED_CATEGORY_LIMIT.getMessage());
    }
}
