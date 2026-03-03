package com.souf.soufwebsite.domain.member.dto.admin.reqDto;

import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record InquiryAnswerReqDto(
        @Schema(description = "답변할 문의글의 아이디", example = "1")
        String answer,

        @Schema(description = "문의글 답변 상태", example = "PENDING or REJECTED or RESOLVED")
        InquiryStatus status
) {
}
