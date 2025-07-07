package com.souf.soufwebsite.global.common.category.exception;

import com.souf.soufwebsite.global.exception.BaseErrorException;

import static com.souf.soufwebsite.global.common.category.exception.ErrorType.NOT_FOUND_CATEGORY_MAPPING;

public class NotFoundCategoryMappingException extends BaseErrorException {
    public NotFoundCategoryMappingException() {
        super(NOT_FOUND_CATEGORY_MAPPING.getCode(), NOT_FOUND_CATEGORY_MAPPING.getMessage());
    }
}
