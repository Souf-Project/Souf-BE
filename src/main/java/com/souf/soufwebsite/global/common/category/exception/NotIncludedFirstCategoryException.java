package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_INCLUDED_FIRST_CATEGORY;

public class NotIncludedFirstCategoryException extends BaseErrorException {
    public NotIncludedFirstCategoryException() {

        super(NOT_INCLUDED_FIRST_CATEGORY.getCode(), NOT_INCLUDED_FIRST_CATEGORY.getMessage());
    }
}