package com.souf.soufwebsite.domain.member.dto.reqDto.admin;

import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;

public record InquiryAnswerReqDto(
        String answer,
        InquiryStatus status
) {
}
