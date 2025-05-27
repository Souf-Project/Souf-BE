package com.souf.soufwebsite.global.common.category.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_INCLUDED_SECOND_CATEGORY(404, "소분류를 사용하기 위해서 중분류가 필요합니다."),
    NOT_INCLUDED_FIRST_CATEGORY(404, "중분류를 사용하기 위해서 대분류가 필요합니다."),

    NOT_MATCHED_CATEGORY(404, "두 조합이 유효하지 않습니다."),

    NOT_FOUND_FIRST_CATEGORY(404, "대분류 카테고리를 찾을 수 없습니다."),
    NOT_FOUND_SECOND_CATEGORY(404, "중분류 카테고리를 찾을 수 없습니다."),
    NOT_FOUND_THIRD_CATEGORY(404, "소분류 카테고리를 찾을 수 없습니다."),

    NOT_DUPLICATE_CATEGORY(400, "이미 존재하는 카테고리입니다."),
    NOT_EXCEED_CATEGORY_LIMIT(400, "카테고리는 최대 3개까지만 등록할 수 있습니다.");

    private final int code;
    private final String message;
}