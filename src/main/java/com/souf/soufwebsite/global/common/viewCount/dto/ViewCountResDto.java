package com.souf.soufwebsite.global.common.viewCount.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ViewCountResDto(
        Long todayVisitor,
        Long studentCount,
        Long recruitCount
) {
    @JsonCreator
    public ViewCountResDto(
            @JsonProperty("todayVisitor") Long todayVisitor,
            @JsonProperty("studentCount") Long studentCount,
            @JsonProperty("recruitCount") Long recruitCount
    ) {
        this.todayVisitor = todayVisitor;
        this.studentCount = studentCount;
        this.recruitCount = recruitCount;
    }
}