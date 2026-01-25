package com.souf.soufwebsite.domain.feed.competition.dto;

public record CompetitionRankResDto(
        Integer rank,
        Long memberId,
        String nickname,
        Long totalLikes
) {
}
