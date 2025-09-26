package com.souf.soufwebsite.domain.recruit.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.souf.soufwebsite.domain.recruit.entity.WorkType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record RecruitReqDto(
        @Schema(description = "공고문 제목", example = "[SW] ggott sw 개발자 모집")
        @NotBlank(message = "공고문 제목은 필수입니다.")
        @Size(min = 2)
        String title,

        @Schema(description = "공고문 내용", example = "공고문 내용...")
        @NotBlank(message = "공고문 내용은 필수입니다.")
        String content,

        @Schema(description = "지역 ID", example = "1")
        @NotNull(message = "지역은 필수입니다.")
        Long cityId,

        @Schema(description = "세부 지역 ID", example = "1")
        Long cityDetailId,

        @Schema(description = "채용 시작일", example = "2025-06-23T13:29")
        @PastOrPresent
        @NotNull(message = "채용 시작일은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime startDate,

        @Schema(description = "마감 기한", example = "2025-06-23T13:29")
        @Future
        @NotNull(message = "마감 기한은 필수입니다.")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime deadline,

        @Schema(description = "최소 제시 금액", example = "100만원")
        String price,

        @Schema(description = "우대사항", example = "1. 전공자 우대\n2. 군필자 우대\n3. Powerpoint를 다루어 본 자")
        String preferentialTreatment,

        @Schema(description = "카테고리 목록", implementation = CategoryDto.class)
        @NotEmpty(message = "적어도 한 개의 카테고리가 들어있어야 합니다.")
        List<CategoryDto> categoryDtos,

        @Schema(description = "기존에 존재하는 파일 URL", example = "[\"https://s3.../img1.jpg\", \"https://s3.../img2.jpg\"]")
        List<String> existingImageUrls,

        @Schema(description = "원본 파일 이름 리스트, 없으면 [] 이렇게 빈 리스트로 반환해주세요.", example = "[\"fileName.jpg\", \"dog.jpg\"]")
        @NotNull(message = "첨부할 파일이 없으면 빈 리스트를 주세요")
        List<String> originalFileNames,

        @Schema(description = "업무 형태를 넣어주세요.", example = "OFFLINE or ONLINE")
        @NotNull(message = "업무 형태를 반드시 지정해주세요.")
        WorkType workType
) {
}
