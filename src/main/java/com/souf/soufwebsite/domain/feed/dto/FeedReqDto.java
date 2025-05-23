package com.souf.soufwebsite.domain.feed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FeedReqDto(

        @Schema(description = "제목", example = "봄 프로젝트 1회차")
        @NotEmpty
        String topic,

        @Schema(description = "피드 내용", example = "오늘 작업 내용...")
        @NotNull
        String content,

        @Schema(description = "해시 태그", example = "[봄, 산책, 나들이, 어린이 대공원]('#'는 빼고 보내주세요!")
        List<String> tags,

        @Schema(description = "원본 파일 이름", example = "[fileName.jpg, dog.jpg..]")
        List<String> originalFileNames
) {
}
