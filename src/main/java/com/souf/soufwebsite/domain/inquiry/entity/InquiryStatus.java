package com.souf.soufwebsite.domain.inquiry.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryStatus {

    PENDING("보류 중"),
    REJECTED("답변 불응 가능"),
    RESOLVED("답변 완료");

    private final String description;
}
