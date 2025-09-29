package com.souf.soufwebsite.domain.review.repository;

import com.souf.soufwebsite.domain.review.dto.ReviewSearchReqDto;
import com.souf.soufwebsite.domain.review.dto.ReviewSimpleResDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ReviewCustomRepository {

    Slice<ReviewSimpleResDto> getReviewBySlice(ReviewSearchReqDto req, Pageable pageable);
}
