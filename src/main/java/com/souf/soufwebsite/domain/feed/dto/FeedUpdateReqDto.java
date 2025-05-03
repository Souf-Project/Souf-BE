package com.souf.soufwebsite.domain.feed.dto;

import java.util.List;

public record FeedUpdateReqDto(
        String content,

        List<Long> keepFileIds
) {}
