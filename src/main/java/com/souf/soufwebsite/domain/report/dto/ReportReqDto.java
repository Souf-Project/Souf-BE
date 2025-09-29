package com.souf.soufwebsite.domain.report.dto;

import com.souf.soufwebsite.global.common.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReportReqDto(

        @NotNull
        @Schema(description = "신고할 게시글의 타입을 정의합니다.", example = "PROFILE, COMMENT, FEED, RECRUIT")
        PostType postType,
        @NotNull
        @Schema(description = "신고할 게시글의 PK를 정의합니다.", example = "1")
        Long postId,
        @Schema(description = "신고할 게시글의 주요 내용을 적습니다.", example = "게시글 제목, 댓글은 내용")
        String title,

        @NotNull
        @Schema(description = "신고하는 사용자의 아이디를 정의합니다.", example = "56")
        Long reporterId,
        @NotNull
        @Schema(description = "신고받는 사용자의 아이디를 정의합니다.", example = "26")
        Long reportedMemberId,

        @NotNull
        @Schema(description = "적합한 신고 사유를 Long으로 지정합니다.", example = "[1, 2, 3]")
        List<Long> reasons,
        @Schema(description = "신고 사유에 대한 자세한 이유를 적습니다.", example = "너무 선정적이예요. 정지 부탁드립니다.")
        String description
) {
}
