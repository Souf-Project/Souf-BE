package com.souf.soufwebsite.domain.member.service;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.util.List;

public interface MemberCategoryService {
    void addCategory(Long memberId, CategoryDto dto);

    void removeCategory(Long memberId, CategoryDto dto);

    void updateCategory(Long memberId, CategoryDto oldDto, CategoryDto newDto);

    List<CategoryDto> getCategoriesOfMember(Long memberId);
}