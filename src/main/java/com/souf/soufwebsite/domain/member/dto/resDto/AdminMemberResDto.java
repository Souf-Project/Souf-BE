package com.souf.soufwebsite.domain.member.dto.resDto;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import io.swagger.v3.oas.annotations.media.Schema;

public record AdminMemberResDto(

        @Schema(description = "회원 아이디", example = "1")
        Long memberId,

        @Schema(description = "회원 권한", example = "MEMBER or STUDENT")
        RoleType roleType,

        @Schema(description = "회원 실명", example = "배성현")
        String username,

        @Schema(description = "회원 닉네임", example = "배나무")
        String nickname,

        @Schema(description = "회원 이메일", example = "example@example.com")
        String email,

        @Schema(description = "회원 누적 신고 횟수", example = "4")
        Integer cumulativeReports,

        @Schema(description = "삭제 여부", example = "FALSE")
        Boolean isDeleted
) {
}
