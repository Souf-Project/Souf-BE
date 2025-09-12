package com.souf.soufwebsite.domain.report.controller;

import com.souf.soufwebsite.domain.report.dto.ReportReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "게시글 신고", description = "사용자 관점에서 작용하는 게시글 신고 기능입니다.")
public interface ReportApiSpecification {

    @Operation(summary = "게시글 신고하기", description = "사용자가 부적절한 게시글을 보고 신고합니다.")
    @PostMapping
    SuccessResponse createReport(
            @CurrentEmail String email,
            @RequestBody @Valid ReportReqDto reportReqDto
            );
}
