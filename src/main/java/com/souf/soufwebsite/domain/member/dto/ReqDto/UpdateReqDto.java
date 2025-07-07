package com.souf.soufwebsite.domain.member.dto.ReqDto;

import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record UpdateReqDto(

        @Schema(description = "프로필 이미지", example = "민수.jpg")
        String profileOriginalFileName,

        @Schema(description = "사용자 실명", example = "홍길동")
        String username,

        @Schema(description = "닉네임", example = "김개똥")
        String nickname,

        @Schema(description = "자기소개", example = "안녕하세요, 디자인 전공자입니다.")
        String intro,

        @Schema(description = "개인 URL", example = "https://github.com/username")
        String personalUrl,

        @Schema(
                description = "수정 후 카테고리 리스트",
                implementation = CategoryDto.class,
                example = """
        [
          { "firstCategory": 1, "secondCategory": 1, "thirdCategory": 1 },
          { "firstCategory": 1, "secondCategory": 1, "thirdCategory": 2 },
          { "firstCategory": 1, "secondCategory": 1, "thirdCategory": 4 }
        ]
        """
        )
        List<CategoryDto> newCategories
) {
}