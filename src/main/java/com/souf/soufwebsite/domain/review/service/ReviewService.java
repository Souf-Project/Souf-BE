package com.souf.soufwebsite.domain.review.service;

import com.souf.soufwebsite.domain.review.dto.ReviewReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewResDto;

public interface ReviewService {

    void createReview(String email, ReviewReqDto reviewReqDto);

    ReviewResDto getDetailedReview(Long reviewId);
}
