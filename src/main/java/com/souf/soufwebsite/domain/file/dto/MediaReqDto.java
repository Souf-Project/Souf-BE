package com.souf.soufwebsite.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record MediaReqDto(
        @Schema(description = "해당 공고문/피드의 아이디", example = "1")
        @NotBlank(message = "아이디는 필수입니다.")
        Long postId,

        @Schema(description = "해당 파일에 접근할 수 있는 url", example = "https://iamsouf.s3.amazonaws.com/feed/original/example.jpg" )
        @NotBlank(message = "url은 필수입니다.")
        List<String> fileUrl,

        @Schema(description = "해당 공고문/피드의 원래 파일 이름", example = "[fileName, pictureName, spring, hihi]")
        @NotNull(message = "파일 이름은 필수입니다.")
        List<String> fileName,

        @Schema(description = "해당 파일의 확장자", example = "[jpg, jpg, png, jpeg]")
        @NotBlank(message = "파일 확장자는 필수입니다.")
        List<String> fileType
) {
}
