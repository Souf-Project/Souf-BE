package com.souf.soufwebsite.domain.feed.competition.service;

import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionRankResDto;
import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionRankRowResDto;
import com.souf.soufwebsite.domain.feed.repository.likedFeed.LikedFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final LikedFeedRepository likedFeedRepository;

    @Cacheable(value = "competitionTop5", key = "'CURRENT'", sync = true)
    @Transactional(readOnly = true)
    @Override
    public List<CompetitionRankResDto> getCurrentCompetitionTop5() {
        log.info("경진대회 순위 서비스 로직 실행");
        LocalDate start = LocalDate.of(2026, 2, 2);
        LocalDate end = LocalDate.of(2026, 2, 28);
        LocalDateTime s = start.atStartOfDay();
        LocalDateTime e = end.atStartOfDay();

        List<CompetitionRankRowResDto> list = likedFeedRepository.findTopAuthorsByLikedInPeriod(s, e);

        ArrayList<CompetitionRankResDto> result = new ArrayList<>();
        int rank = 1;
        for (CompetitionRankRowResDto c : list) {
            //log.info("{}, {}, {}", rank, c.memberId(), c.totalLikes());
            result.add(new CompetitionRankResDto(rank++, c.memberId(), c.nickname(), c.totalLikes()));
        }

        return result;
    }
}
