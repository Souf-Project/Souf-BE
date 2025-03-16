package com.souf.soufwebsite.domain.feed.dto;

import jakarta.validation.constraints.NotNull;

public record FeedReqDto (
        @NotNull
        String content
) {
}
