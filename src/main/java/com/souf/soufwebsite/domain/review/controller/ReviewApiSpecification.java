package com.souf.soufwebsite.domain.review.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.review.dto.*;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Review", description = "후기 관련 API")
public interface ReviewApiSpecification {

    @Operation(summary = "후기 생성", description = "외주 작업이 완료된 사용자가 후기를 작성합니다.")
    @PostMapping
    SuccessResponse<ReviewCreatedResDto> createReview(
            @CurrentEmail String email,
            @Valid @RequestBody ReviewReqDto reviewReqDto
    );

    @Operation(summary = "후기 관련 파일 메타데이터 저장", description = "외주 후기 생성 후 업로드된 파일들의 메타데이터를 저장합니다.")
    @PostMapping("/upload")
    SuccessResponse<?> uploadReviewMedia(
            @CurrentEmail String email,
            @Valid @RequestBody MediaReqDto mediaReqDto
    );

    @Operation(summary = "후기 리스트 조회", description = "필터링된 후기 리스트들을 조회합니다.")
    @PostMapping("/search")
    SuccessResponse<Slice<ReviewSimpleResDto>> getReviews(
            @RequestBody ReviewSearchReqDto searchReqDto,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "후기 상세 조회", description = "특정 후기를 대해 상세 조회합니다.")
    @GetMapping("/{reviewId}")
    SuccessResponse<ReviewDetailedResDto> getDetailedReview(
            @PathVariable(value = "reviewId") Long reviewId,
            HttpServletRequest request
    );

    @Operation(summary = "후기 수정", description = "작성 후기를 수정합니다.")
    @PatchMapping("/{reviewId}")
    SuccessResponse<ReviewCreatedResDto> updateReview(
            @CurrentEmail String email,
            @PathVariable(value = "reviewId") Long reviewId,
            @Valid @RequestBody ReviewReqDto reviewReqDto
    );

    @Operation(summary = "후기 삭제", description = "작성한 후기를 삭제합니다.")
    @DeleteMapping("/{reviewId}")
    SuccessResponse<?> deleteReview(
            @CurrentEmail String email,
            @PathVariable(value = "reviewId") Long reviewId
    );
}
