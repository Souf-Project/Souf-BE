package com.souf.soufwebsite.domain.review.controller;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewCreatedResDto;
import com.souf.soufwebsite.domain.review.dto.ReviewDetailedResDto;
import com.souf.soufwebsite.domain.review.dto.ReviewReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewSimpleResDto;
import com.souf.soufwebsite.domain.review.service.ReviewService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.review.controller.ReviewSuccessMessage.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/review")
public class ReviewController implements ReviewApiSpecification {

    private final ReviewService reviewService;

    @PostMapping
    public SuccessResponse<ReviewCreatedResDto> createReview(
            @CurrentEmail String email,
            @Valid @RequestBody ReviewReqDto reviewReqDto) {


        return new SuccessResponse<>(reviewService.createReview(email, reviewReqDto), REVIEW_CREATE.getMessage());
    }

    @PostMapping("/upload")
    public SuccessResponse<?> uploadReviewMedia(
            @CurrentEmail String email,
            @Valid @RequestBody MediaReqDto mediaReqDto) {

        reviewService.uploadReviewMedia(email, mediaReqDto);

        return new SuccessResponse<>(REVIEW_FILE_METADATA_CREATE.getMessage());
    }

    @GetMapping
    public SuccessResponse<Slice<ReviewSimpleResDto>> getReviews(
            @PageableDefault Pageable pageable) {
        return null;
    }

    @GetMapping("/{reviewId}")
    public SuccessResponse<ReviewDetailedResDto> getDetailedReview(
            @PathVariable(value = "reviewId") Long reviewId,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        ReviewDetailedResDto detailedReview = reviewService.getDetailedReview(reviewId, ip, userAgent);
        return new SuccessResponse<>(detailedReview, REVIEW_GET.getMessage());
    }

    @PatchMapping("/{reviewId}")
    public SuccessResponse<ReviewCreatedResDto> updateReview(
            @CurrentEmail String email,
            @PathVariable(value = "reviewId") Long reviewId,
            @Valid @RequestBody ReviewReqDto reviewReqDto) {

        ReviewCreatedResDto reviewCreatedResDto = reviewService.updateReview(email, reviewId, reviewReqDto);
        return new SuccessResponse<>(reviewCreatedResDto, REVIEW_UPDATE.getMessage());
    }

    @DeleteMapping("/{reviewId}")
    public SuccessResponse<?> deleteReview(
            @CurrentEmail String email,
            @PathVariable(value = "reviewId") Long reviewId) {

        reviewService.deleteReview(email, reviewId);

        return new SuccessResponse<>(REVIEW_DELETE.getMessage());
    }
}
