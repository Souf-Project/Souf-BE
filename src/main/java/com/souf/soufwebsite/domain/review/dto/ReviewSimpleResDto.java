package com.souf.soufwebsite.domain.review.dto;

import com.souf.soufwebsite.domain.review.entity.Review;

import java.time.LocalDate;

public record ReviewSimpleResDto(
        String content,
        Double score,
        LocalDate createdDate,
        String postUrl,

        String nickname, // 후기 작성자
        String recruitTitle // 공고문 제목
) {

    public static ReviewSimpleResDto from(Review review, String reviewImgUrl, String nickname, String recruitTitle) {
        return new ReviewSimpleResDto(
                review.getContent(),
                review.getScore(),
                review.getCreatedTime().toLocalDate(),
                reviewImgUrl,
                nickname,
                recruitTitle
        );
    }
}
