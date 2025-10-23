package com.souf.soufwebsite.domain.inquiry.dto;

import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;

import java.time.LocalDateTime;

public record InquiryResDto(
        Long inquiryId,
        Long inquiryType,
        String title,
        String content,
        String writer,
        LocalDateTime createdTime,
        InquiryStatus status
) {
    public static InquiryResDto of(Inquiry inquiry) {
        return new InquiryResDto(
                inquiry.getId(),
                inquiry.getInquiryType().getType(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getMember().getNickname(),
                inquiry.getCreatedTime(),
                inquiry.getInquiryStatus()
        );
    }
}
