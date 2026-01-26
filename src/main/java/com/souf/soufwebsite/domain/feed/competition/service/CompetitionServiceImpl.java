package com.souf.soufwebsite.domain.feed.competition.service;

import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionFeedRowResDto;
import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionPopularFeedResDto;
import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionRankResDto;
import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionRankRowResDto;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.feed.repository.likedFeed.LikedFeedRepository;
import com.souf.soufwebsite.domain.file.dto.MediaResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.global.common.PostType;
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
    private final FeedRepository feedRepository;

    private final FileService fileService;

    static LocalDateTime s;
    static LocalDateTime e;

    @Cacheable(value = "competitionTop5", key = "'CURRENT'", sync = true)
    @Transactional(readOnly = true)
    @Override
    public List<CompetitionRankResDto> getCurrentCompetitionTop5() {
        log.info("경진대회 순위 서비스 로직 실행");
        LocalDate start = LocalDate.of(2025, 8, 2);
        LocalDate end = LocalDate.of(2026, 2, 28);
        s = start.atStartOfDay();
        e = end.atStartOfDay();

        List<CompetitionRankRowResDto> list = likedFeedRepository.findTopAuthorsByLikedInPeriod(s, e);

        return applyDenseRank(list);
    }

    private List<CompetitionRankResDto> applyDenseRank(List<CompetitionRankRowResDto> rows) {
        List<CompetitionRankResDto> res = new ArrayList<>();
        int rank = 0;
        Long prevLikes = null;

        for (CompetitionRankRowResDto row : rows) {
            long likes = row.totalLikes();
            if (prevLikes == null || likes != prevLikes) {
                rank += 1;
                prevLikes = likes;
            }
            List<CompetitionFeedRowResDto> feedList = feedRepository.findTop3ByMemberOrderByFeedLikes(row.memberId(), s, e);
            List<CompetitionPopularFeedResDto> feedElements = feedList.stream()
                    .map(element -> {
                        List<Media> mediaList = fileService.getMediaList(PostType.FEED, element.feedId());
                        MediaResDto mediaResDto = mediaList.isEmpty() ? null : MediaResDto.fromMedia(mediaList.get(0));
                        return CompetitionPopularFeedResDto.of(element.feedId(), element.feedTitle(), mediaResDto);
                    }).toList();
            res.add(new CompetitionRankResDto(rank, row.memberId(), row.nickname(), likes, feedElements));
        }
        return res; // rows가 이미 limit 5라 결과도 5명 고정
    }
}
