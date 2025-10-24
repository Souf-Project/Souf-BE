package com.souf.soufwebsite.domain.inquiry.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryCreateResDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryDetailedResDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryReqDto;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Inquiry", description = "문의글 관련 API")
public interface InquiryApiSpecification {

    @Operation(summary = "문의글 생성", description = "회원이 문의글을 생성합니다.")
    @PostMapping
    SuccessResponse<InquiryCreateResDto> createInquiry (
            @CurrentEmail String email,
            @RequestBody InquiryReqDto reqDto);

    @PostMapping("/upload")
    SuccessResponse<?> uploadMetadata (
            @CurrentEmail String email,
            @Valid @RequestBody MediaReqDto reqDto
    );

    @Operation(summary = "문의글 수정", description = "회원이 본인 소유의 문의글을 수정합니다.")
    @PatchMapping("/{inquiryId}")
    SuccessResponse<?> updateInquiry(
            @CurrentEmail String email,
            @RequestBody InquiryReqDto reqDto,
            @PathVariable(name = "inquiryId") Long inquiryId
    );

    @Operation(summary = "문의글 삭제", description = "회원이 본의 소유의 문의글을 삭제합니다.")
    @DeleteMapping("/{inquiryId}")
    SuccessResponse<?> deleteInquiry(
            @CurrentEmail String email,
            @PathVariable(name = "inquiryId") Long inquiryId
    );

    @Operation(summary = "내 문의글 조회", description = "회원이 본인 소유의 문의글 리스트를 조회합니다.")
    @GetMapping("/my")
    SuccessResponse<Page<InquiryResDto>> getMyInquiry(
            @CurrentEmail String email,
            @PageableDefault(size = 8) Pageable pageable
    );

    @GetMapping("/{inquiryId}")
    SuccessResponse<InquiryDetailedResDto> getInquiry(
            @PathVariable(name = "inquiryId") Long inquiryId,
            @CurrentEmail String email
    );
}
