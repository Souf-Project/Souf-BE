package com.souf.soufwebsite.domain.recruit.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecruitSearchReqDto(
        @Schema(description = "제목")
        String title,
        @Schema(description = "내용")
        String content,
        @Schema(description = "정렬 키 (RECENT, VIEWS, PAYMENT)", example = "RECENT")
        SortKey sortKey,     // 추가
        @Schema(description = "정렬 방향 (ASC, DESC)", example = "DESC")
        SortDir sortDir      // 추가
) {
        public enum SortKey { RECENT, VIEWS, PAYMENT }
        public enum SortDir { ASC, DESC }

        // null 방지 기본값 헬퍼
        public SortKey sortKeyOrDefault() { return sortKey == null ? SortKey.RECENT : sortKey; }
        public SortDir sortDirOrDefault() { return sortDir == null ? SortDir.DESC : sortDir; }
}