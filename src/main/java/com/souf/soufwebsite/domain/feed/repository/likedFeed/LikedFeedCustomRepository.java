package com.souf.soufwebsite.domain.feed.repository.likedFeed;

import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionRankRowResDto;

import java.time.LocalDate;
import java.util.List;

public interface LikedFeedCustomRepository {

    List<CompetitionRankRowResDto> findTopAuthorsByLikedInPeriod(
            LocalDate start, LocalDate end
    );
}
