package com.souf.soufwebsite.domain.feed.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionFeedRowResDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.souf.soufwebsite.domain.feed.entity.QFeed.feed;
import static com.souf.soufwebsite.domain.feed.entity.QFeedCategoryMapping.feedCategoryMapping;
import static com.souf.soufwebsite.domain.feed.entity.QLikedFeed.likedFeed;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedCustomRepositoryImpl implements FeedCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Slice<Feed> findByFirstCategoryOrderByCreatedTimeDesc(Long first, Pageable pageable) {

        List<Long> feedIds = queryFactory
                .select(feed.id)
                .from(feed)
                .join(feed.categories, feedCategoryMapping)
                .where(first != null ? feedCategoryMapping.firstCategory.id.eq(first) : null)
                .orderBy(feed.createdTime.desc(), feed.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();
        log.info("size: {}", feedIds.size());

        List<Feed> feeds = queryFactory
                .selectFrom(feed)
                .leftJoin(feed.categories, feedCategoryMapping).fetchJoin()
                .where(feed.id.in(feedIds.subList(0, Math.min(feedIds.size(), pageable.getPageSize()))))
                .orderBy(feed.createdTime.desc(), feed.id.desc())
                .fetch();

        boolean hasNextPage = feedIds.size() > pageable.getPageSize();

        return new SliceImpl<>(feeds, pageable, hasNextPage);
    }

    @Override
    public List<CompetitionFeedRowResDto> findTop3ByMemberOrderByFeedLikes(Long mId, LocalDateTime s, LocalDateTime e) {

        NumberExpression<Long> likeCnt = likedFeed.id.count();

        return queryFactory
                .select(
                        Projections.constructor(
                                CompetitionFeedRowResDto.class,
                                feed.id,
                                feed.topic,
                                likeCnt
                        )
                ).from(feed)
                .leftJoin(likedFeed).on(
                        feed.id.eq(likedFeed.feedId),
                        likedFeed.createdTime.goe(s),
                        likedFeed.createdTime.lt(e))
                .where(feed.member.id.eq(mId))
                .groupBy(feed.id, feed.topic)
                .orderBy(likeCnt.desc(), feed.id.asc())
                .limit(3)
                .fetch();
    }
}

