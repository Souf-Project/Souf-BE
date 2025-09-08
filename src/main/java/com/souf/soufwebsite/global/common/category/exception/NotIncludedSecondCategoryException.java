package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_INCLUDED_SECOND_CATEGORY;

public class NotIncludedSecondCategoryException extends BaseErrorException {
    public NotIncludedSecondCategoryException() {
        super(NOT_INCLUDED_SECOND_CATEGORY.getCode(), NOT_INCLUDED_SECOND_CATEGORY.getMessage(), NOT_INCLUDED_SECOND_CATEGORY.getErrorKey());
    }
}
