package com.souf.soufwebsite.domain.application.controller;

import com.souf.soufwebsite.domain.application.dto.ApplicantResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public interface RecruitApplicationApiSpecification {
    @Tag(name = "Application", description = "지원 관련 API")
    @Operation(summary = "특정 공고문 지원자 리스트 조회", description = "특정 공고문에 지원한 지원자들의 리스트를 조회합니다.")
    @GetMapping("/{recruitId}/applicants")
    SuccessResponse<Page<ApplicantResDto>> getApplicantsForRecruit(
            @PathVariable Long recruitId,
            @PageableDefault(size = 10) Pageable pageable
    );

    @Tag(name = "Application", description = "지원 관련 API")
    @Operation(summary = "지원 승인", description = "특정 지원자의 지원을 승인합니다.")
    @PostMapping("/{applicationId}/approve")
    SuccessResponse<?> reviewApplication(@PathVariable Long applicationId);

    @Tag(name = "Application", description = "지원 관련 API")
    @Operation(summary = "지원 거절", description = "특정 지원자의 지원을 거절합니다.")
    @PostMapping("/{applicationId}/reject")
    SuccessResponse<?> rejectApplication(@PathVariable Long applicationId);
}
