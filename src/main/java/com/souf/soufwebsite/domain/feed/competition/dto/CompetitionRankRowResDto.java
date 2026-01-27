package com.souf.soufwebsite.domain.feed.competition.dto;

public record CompetitionRankRowResDto(
        Long memberId,
        String nickname,
        long totalLikes
) {
}
