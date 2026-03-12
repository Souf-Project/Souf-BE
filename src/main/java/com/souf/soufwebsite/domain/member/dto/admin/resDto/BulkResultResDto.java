package com.souf.soufwebsite.domain.member.dto.admin.resDto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record BulkResultResDto(
        @Schema(description = "처리 요청한 회원의 총 수", example = "10")
        int requested,

        @Schema(description = "실제로 처리된 회원의 수", example = "8")
        int processed,

        @Schema(description = "처리 요청한 회원 중 존재하지 않는 회원의 아이디 리스트", example = "[4, 5]")
        List<Long> notFound,

        @Schema(description = "처리 요청한 회원 중 이미 처리된 회원의 아이디 리스트", example = "[6, 7]")
        List<Long> alreadyProcessed,

        @Schema(description = "처리 요청한 회원 중 처리 과정에서 오류가 발생한 회원의 아이디와 오류 코드 리스트", example = "[{id: 8, code: 'DB_ERROR'}, {id: 9, code: 'UNKNOWN_ERROR'}]")
        List<FailedItem> failed
) {
    public record FailedItem(Long id, String code) {}
}
