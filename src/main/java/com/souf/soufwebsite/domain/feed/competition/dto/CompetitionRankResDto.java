package com.souf.soufwebsite.domain.feed.competition.dto;

public record CompetitionRankResDto(
        int rank,
        long memberId,
        String nickname,
        long totalLikes
) {
}
