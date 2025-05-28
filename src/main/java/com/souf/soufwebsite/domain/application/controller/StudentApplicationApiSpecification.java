package com.souf.soufwebsite.domain.application.controller;

import com.souf.soufwebsite.domain.application.dto.MyApplicationResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Application", description = "지원 관련 API")
public interface StudentApplicationApiSpecification {

    @Operation(summary = "지원하기", description = "특정 공고문에 지원합니다.")
    @PostMapping("/{recruitId}/apply")
    SuccessResponse<?> apply(@PathVariable Long recruitId);

    @Operation(summary = "지원 취소", description = "특정 공고문에 대한 지원을 취소합니다.")
    @DeleteMapping("/{recruitId}/apply")
    SuccessResponse<?> deleteApplication(@PathVariable Long recruitId);

    @Operation(summary = "내 지원 리스트 조회", description = "사용자 본인이 지원한 공고문 리스트를 조회합니다.")
    @GetMapping("/my")
    SuccessResponse<Page<MyApplicationResDto>> getMyApplications(@PageableDefault(size = 10) Pageable pageable);
}
