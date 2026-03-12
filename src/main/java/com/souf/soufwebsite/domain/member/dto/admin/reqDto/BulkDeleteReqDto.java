package com.souf.soufwebsite.domain.member.dto.admin.reqDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BulkDeleteReqDto(
        @Schema(description = "삭제할 회원들의 아이디 리스트", example = "[1, 2, 3]")
        List<Long> ids,

        @Schema(description = "삭제 사유", example = "서비스 이용 약관 위반")
        String reason
) {}
