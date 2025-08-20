package com.souf.soufwebsite.domain.member.controller.memberCategory;

import com.souf.soufwebsite.domain.member.service.general.MemberCategoryService;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.dto.CategoryUpdateReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.souf.soufwebsite.domain.member.controller.memberCategory.MemberCategorySuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member/category")
public class MemberCategoryController implements MemberCategoryApiSpecification{

    private final MemberCategoryService memberCategoryService;

    @PostMapping("/{memberId}")
    public SuccessResponse addCategory(@PathVariable Long memberId, @Valid @RequestBody CategoryDto dto) {
        memberCategoryService.addCategory(memberId, dto);
        return new SuccessResponse(CATEGORY_ADD_SUCCESS.getMessage());
    }

    @DeleteMapping("/{memberId}")
    public SuccessResponse removeCategory(@PathVariable Long memberId, @Valid @RequestBody CategoryDto dto) {
        memberCategoryService.removeCategory(memberId, dto);
        return new SuccessResponse(CATEGORY_REMOVE_SUCCESS.getMessage());
    }

    @PutMapping("/{memberId}")
    public SuccessResponse updateCategory(@PathVariable Long memberId, @Valid @RequestBody CategoryUpdateReqDto dto) {
        memberCategoryService.updateCategory(memberId, dto.oldCategory(), dto.newCategory());
        return new SuccessResponse(CATEGORY_UPDATE_SUCCESS.getMessage());
    }

    @GetMapping("/{memberId}")
    public SuccessResponse<List<CategoryDto>> getCategories(@PathVariable Long memberId) {
        List<CategoryDto> categoryList = memberCategoryService.getCategoriesOfMember(memberId);
        return new SuccessResponse<>(categoryList, CATEGORY_GET_SUCCESS.getMessage());
    }
}