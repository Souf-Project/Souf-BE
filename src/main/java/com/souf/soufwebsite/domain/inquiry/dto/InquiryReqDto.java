package com.souf.soufwebsite.domain.inquiry.dto;

import com.souf.soufwebsite.domain.inquiry.entity.InquiryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record InquiryReqDto(
        @Schema(description = "문의 제목", example = "문의 제목을 적어주세요")
        @NotBlank(message = "문의 제목은 필수입니다.")
        String title,

        @Schema(description = "문의 내용", example = "문의 내용을 적어주세요")
        @NotBlank(message = "문의 내용은 20자 이상이 필수입니다.")
        @Size(min = 20)
        String content,

        @Schema(description = "문의 유형", example = "문의 제목을 적어주세요")
        @NotNull(message = "문의 유형은 필수입니다.")
        InquiryType type,

        @Schema(description = "기존에 존재하는 파일 URL", example = "[\"feed/original/...\", \"feed/original/..\"]")
        List<String> existingImageUrls,

        @Schema(description = "원본 파일 이름", example = "[\"fileName.jpg\", \"dog.jpg\"..]")
        List<String> originalFileNames
) {
}
