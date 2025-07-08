package com.souf.soufwebsite.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ChatFileReqDto(
        @Schema(description = "원본 파일 이름", example = "[fileName.jpg, dog.jpg..]")
        List<String> originalFileNames
) {
}
