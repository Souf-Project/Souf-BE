package com.souf.soufwebsite.domain.member.controller.memberCategory;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.category.dto.CategoryUpdateReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카테고리 (관심 분야)", description = "회원 카테고리 관리 API")
public interface MemberCategoryApiSpecification {

    @Operation(summary = "카테고리 추가", description = "회원에게 1~3차 카테고리를 추가합니다.")
    @PostMapping("/{memberId}")
    SuccessResponse<?> addCategory(
            @PathVariable Long memberId,
            @Valid @RequestBody CategoryDto dto
    );

    @Operation(summary = "카테고리 삭제", description = "회원의 카테고리 정보를 삭제합니다.")
    @DeleteMapping("/{memberId}")
    SuccessResponse<?> removeCategory(
            @PathVariable Long memberId,
            @Valid @RequestBody CategoryDto dto
    );

    @Operation(summary = "카테고리 수정", description = "회원의 카테고리를 다른 값으로 수정합니다.")
    @PutMapping("/{memberId}")
    SuccessResponse<?> updateCategory(
            @PathVariable Long memberId,
            @Valid @RequestBody CategoryUpdateReqDto dto
    );

    @Operation(summary = "카테고리 목록 조회", description = "회원이 설정한 카테고리 목록을 조회합니다.")
    @GetMapping("/{memberId}")
    SuccessResponse<List<CategoryDto>> getCategories(
            @PathVariable Long memberId
    );
}