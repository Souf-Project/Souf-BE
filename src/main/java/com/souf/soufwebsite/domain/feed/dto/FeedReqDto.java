package com.souf.soufwebsite.domain.feed.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FeedReqDto(

        @NotEmpty
        String topic,

        @NotNull
        String content,
        List<String> tags,
        List<String> originalFileNames
) {
}
