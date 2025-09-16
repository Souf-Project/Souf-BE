package com.souf.soufwebsite.domain.recruit.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyRecruitReqDto(
        @Schema(description = "정렬 키 (RECENT, VIEWS(조회수), COUNT(지원자수))", example = "RECENT")
        MySortKey sortKey,
        @Schema(description = "정렬 방향 (ASC, DESC)", example = "DESC")
        MySortDir sortDir
) {
    public enum MySortKey { RECENT, VIEWS, COUNT }
    public enum MySortDir { ASC, DESC }

    // null 방지 기본값 헬퍼
    public MySortKey sortKeyOrDefault() { return sortKey == null ? MySortKey.RECENT : sortKey; }
    public MySortDir sortDirOrDefault() { return sortDir == null ? MySortDir.DESC : sortDir; }
}