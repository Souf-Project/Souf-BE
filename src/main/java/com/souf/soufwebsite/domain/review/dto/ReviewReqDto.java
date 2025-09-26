package com.souf.soufwebsite.domain.review.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ReviewReqDto(

        @NotBlank(message = "제목은 10자 이상 입력해주셔야 합니다.")
        @Size(min = 10)
        String title,

        @NotBlank(message = "후기 내용은 20자 이상 입력해주셔야 합니다.")
        @Size(min = 20)
        String content,

        @NotNull(message = "평가 점수를 부여해주세요.")
        Double score,

        @NotNull(message = "매핑된 공고문 아이디를 기입해주세요.")
        Long recruitId,

        @NotNull(message = "후기 작성에 필요한 파일은 하나 이상 필요합니다.")
        List<String> originalFileNames,

        @Schema(description = "기존에 존재하는 파일 URL", example = "[\"review/original/...\", \"review/original/..\"]")
        List<String> existingImageUrls
) {
}
