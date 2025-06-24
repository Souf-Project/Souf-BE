package com.souf.soufwebsite.global.common.category.dto;

import jakarta.validation.constraints.NotNull;

public record CategoryUpdateReqDto(
        @NotNull
        CategoryDto oldCategory,
        @NotNull
        CategoryDto newCategory) {
}
