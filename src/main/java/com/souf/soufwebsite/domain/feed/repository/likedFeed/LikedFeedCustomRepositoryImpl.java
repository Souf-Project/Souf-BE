package com.souf.soufwebsite.domain.feed.repository.likedFeed;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionRankRowResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.souf.soufwebsite.domain.feed.entity.QFeed.feed;
import static com.souf.soufwebsite.domain.feed.entity.QLikedFeed.likedFeed;
import static com.souf.soufwebsite.domain.member.entity.QMember.member;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikedFeedCustomRepositoryImpl implements LikedFeedCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CompetitionRankRowResDto> findTopAuthorsByLikedInPeriod(LocalDate start, LocalDate end) {

         return queryFactory
                .select(Projections.constructor(
                        CompetitionRankRowResDto.class,
                        member.id,
                        member.nickname,
                        likedFeed.id.count()
                )).from(likedFeed)
                .join(feed).on(likedFeed.feedId.eq(feed.id))
                .join(feed.member, member)
                .where()
                .groupBy(member.id)
                .orderBy(likedFeed.id.count().desc(), member.id.asc())
                .limit(5)
                .fetch();
    }
}
