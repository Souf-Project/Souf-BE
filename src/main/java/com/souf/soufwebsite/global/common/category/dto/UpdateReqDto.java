package com.souf.soufwebsite.global.common.category.dto;

public record UpdateReqDto(
        CategoryDto oldCategory,
        CategoryDto newCategory) {
}
