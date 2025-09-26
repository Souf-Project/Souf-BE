package com.souf.soufwebsite.domain.review.service;

import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewCreatedResDto;
import com.souf.soufwebsite.domain.review.dto.ReviewDetailedResDto;
import com.souf.soufwebsite.domain.review.dto.ReviewReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewSimpleResDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewService {

    ReviewCreatedResDto createReview(String email, ReviewReqDto reviewReqDto);

    void uploadReviewMedia(String email, MediaReqDto mediaReqDto);

    Slice<ReviewSimpleResDto> getReviews(Pageable pageable);

    ReviewDetailedResDto getDetailedReview(Long reviewId, String ip, String userAgent);

    ReviewCreatedResDto updateReview(String email, Long reviewId, ReviewReqDto reviewReqDto);

    void deleteReview(String email, Long reviewId);
}
