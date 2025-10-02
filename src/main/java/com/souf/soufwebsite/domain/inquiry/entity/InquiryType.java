package com.souf.soufwebsite.domain.inquiry.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquiryType {

    RELATED_FEED(1L, "피드 관련 문의"),
    RELATED_RECRUIT(2L, "공고문 관련 문의"),
    RELATED_REVIEW(3L, "후기 관련 문의"),
    RELATED_CHAT(4L, "채팅 관련 문의"),
    RELATED_AUTHENTICATION(5L, "계정/인증 관련 문의"),
    ETC(6L, "기타 문의 사항");

    private final Long type;
    private final String description;
}
