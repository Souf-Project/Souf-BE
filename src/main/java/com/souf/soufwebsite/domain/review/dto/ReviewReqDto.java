package com.souf.soufwebsite.domain.review.dto;

public record ReviewReqDto(
        String title,
        String content,
        Double score,
        Long recruitId
) {
}
