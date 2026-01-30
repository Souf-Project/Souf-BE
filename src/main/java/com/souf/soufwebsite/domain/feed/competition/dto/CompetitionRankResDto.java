package com.souf.soufwebsite.domain.feed.competition.dto;

import java.util.List;

public record CompetitionRankResDto(
        Integer rank,
        Long memberId,
        String nickname,
        Long totalLikes,
        List<CompetitionPopularFeedResDto> competitionPopularFeedResDto
) {
}
