package com.souf.soufwebsite.domain.feed.dto;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FeedReqDto(

        @Schema(description = "제목", example = "봄 프로젝트 1회차")
        @NotEmpty(message = "제목은 한 자 이상 반드시 기입해주셔야 합니다.")
        String topic,

        @Schema(description = "피드 내용", example = "오늘 작업 내용...")
        @NotNull
        String content,

        @Schema(description = "기존에 존재하는 파일 URL", example = "[\"feed/original/...\", \"feed/original/..\"]")
        List<String> existingImageUrls,

        @Schema(description = "원본 파일 이름", example = "[fileName.jpg, dog.jpg..]")
        @NotNull(message = "첨부할 이미지가 없더라도 빈 값을 넣어주세요.")
        List<String> originalFileNames,

        @Schema(description = "카테고리 목록", implementation = CategoryDto.class)
        @NotNull(message = "적어도 한 개의 카테고리가 들어있어야 합니다.")
        List<CategoryDto> categoryDtos
        ) {
}
