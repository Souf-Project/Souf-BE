package com.souf.soufwebsite.domain.review.service;

import com.souf.soufwebsite.domain.review.dto.ReviewReqDto;

public interface ReviewService {

    void createReview(String email, ReviewReqDto reviewReqDto);
}
