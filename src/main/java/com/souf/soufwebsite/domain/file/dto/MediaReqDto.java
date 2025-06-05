package com.souf.soufwebsite.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record MediaReqDto(
        @Schema(description = "해당 공고문/피드의 아이디", example = "1")
        Long postId,

        @Schema(description = "해당 파일에 접근할 수 있는 url", example = "https://iamsouf.s3.amazonaws.com/feed/original/example.jpg" )
        List<String> fileUrl,

        @Schema(description = "해당 공고문/피드의 원래 파일 이름", example = "[fileName, pictureName, spring, hihi]")
        List<String> fileName,

        @Schema(description = "해당 공고문/피드의 아이디", example = "[jpg, jpg, png, jpeg]")
        List<String> fileType
) {
}
