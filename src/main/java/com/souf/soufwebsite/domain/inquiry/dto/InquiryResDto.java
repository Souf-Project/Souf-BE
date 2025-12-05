package com.souf.soufwebsite.domain.inquiry.dto;

import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;
import com.souf.soufwebsite.domain.member.entity.Member;

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
        Member writer = inquiry.getMember();

        String nickname = (writer == null)
                ? "탈퇴한 회원"
                : writer.getNickname();

        return new InquiryResDto(
                inquiry.getId(),
                inquiry.getInquiryType().getType(),
                inquiry.getTitle(),
                inquiry.getContent(),
                nickname,
                inquiry.getCreatedTime(),
                inquiry.getInquiryStatus()
        );
    }
}
