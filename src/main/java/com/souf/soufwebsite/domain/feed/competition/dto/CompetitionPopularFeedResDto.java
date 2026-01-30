package com.souf.soufwebsite.domain.feed.competition.dto;

import com.souf.soufwebsite.domain.file.dto.MediaResDto;

public record CompetitionPopularFeedResDto(
        Long feedId,
        String feedTitle,
        MediaResDto mediaResDto
) {

    public static CompetitionPopularFeedResDto of(Long feedId, String feedTitle, MediaResDto mediaResDto) {
        return new CompetitionPopularFeedResDto(feedId, feedTitle, mediaResDto);
    }
}
