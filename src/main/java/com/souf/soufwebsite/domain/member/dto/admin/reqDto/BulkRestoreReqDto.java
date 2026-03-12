package com.souf.soufwebsite.domain.member.dto.admin.reqDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BulkRestoreReqDto(
        @Schema(description = "복구할 회원들의 아이디 리스트", example = "[1, 2, 3]")
        List<Long> ids
) {}