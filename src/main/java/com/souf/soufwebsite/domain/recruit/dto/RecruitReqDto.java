package com.souf.soufwebsite.domain.recruit.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.souf.soufwebsite.domain.recruit.entity.RegionType;
import com.souf.soufwebsite.domain.recruit.entity.WorkType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record RecruitReqDto(
        @Schema(description = "공고문 제목", example = "[SW] ggott sw 개발자 모집")
        @NotEmpty(message = "공고문 제목은 필수입니다.")
        @Size(min = 2)
        String title,

        @Schema(description = "공고문 내용", example = "공고문 내용...")
        @NotNull(message = "공고문 내용은 필수입니다.")
        @Size(max = 300)
        String content,

        @Schema(description = "지역", example = "광진구")
        @NotNull(message = "진행 지역은 필수입니다.")
        @Size(max = 30)
        RegionType region,

        @Schema(description = "마감 기한", example = "2025-06-23T13:29")
        @Future
        @NotNull(message = "마감 기한은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime deadline,

        @Schema(description = "제시 금액", example = "100만원")
        @NotEmpty(message = "제시 금액은 필수입니다.")
        String payment,

        @Schema(description = "우대사항", example = "1. 전공자 우대\n2. 군필자 우대\n3. Powerpoint를 다루어 본 자")
        @NotNull(message = "우대사항은 옵션입니다.")
        @Size(max = 300)
        String preferentialTreatment,

        @Schema(description = "카테고리 목록", implementation = CategoryDto.class)
        @NotNull(message = "적어도 한 개의 카테고리가 들어있어야 합니다.")
        List<CategoryDto> categoryDtos,

        @Schema(description = "원본 파일 이름 리스트, 없으면 [] 이렇게 빈 리스트로 반환해주세요.", example = "[\"fileName.jpg\", \"dog.jpg\"]")
        List<String> originalFileNames,

        @Schema(description = "업무 형태를 넣어주세요.", example = "OFFLINE or ONLINE")
        @NotNull(message = "업무 형태를 반드시 지정해주세요.")
        WorkType workType
) {
}
