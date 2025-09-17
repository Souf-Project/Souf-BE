package com.souf.soufwebsite.domain.recruit.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SortOption<T extends Enum<T>>(
        @Schema(description = "정렬 키", example = "RECENT")
        T sortKey,
        @Schema(description = "정렬 방향 (ASC, DESC)", example = "DESC")
        SortDir sortDir
) {
    public enum SortDir { ASC, DESC }

    public T sortKeyOrDefault(T defaultKey) {
        return sortKey == null ? defaultKey : sortKey;
    }

    public SortDir sortDirOrDefault() {
        return sortDir == null ? SortDir.DESC : sortDir;
    }
}