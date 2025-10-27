package com.souf.soufwebsite.domain.member.dto.reqDto;

import io.swagger.v3.oas.annotations.media.Schema;

public record MemberSearchReqDto(
        @Schema(description = "검색어", example = "디자인")
        String keyword
) {
}
