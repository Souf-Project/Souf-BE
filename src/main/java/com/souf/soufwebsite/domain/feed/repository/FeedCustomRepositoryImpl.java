package com.souf.soufwebsite.domain.feed.repository;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionFeedRowResDto;
import com.souf.soufwebsite.domain.feed.dto.req.FeedSearchReqDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.entity.FeedSortKey;
import com.souf.soufwebsite.global.common.sort.dto.SortOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.souf.soufwebsite.domain.feed.entity.QFeed.feed;
import static com.souf.soufwebsite.domain.feed.entity.QFeedCategoryMapping.feedCategoryMapping;
import static com.souf.soufwebsite.domain.feed.entity.QLikedFeed.likedFeed;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedCustomRepositoryImpl implements FeedCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Feed> getFeedList(FeedSearchReqDto req, Pageable pageable) {

        Long first = (req == null) ? null : req.firstCategory();
        SortOption<FeedSortKey> sortOption = (req == null || req.sortOption() == null)
                ? new SortOption<>(FeedSortKey.RECENT, SortOption.SortDir.DESC)
                : req.sortOption();

        FeedSortKey key = sortOption.sortKeyOrDefault(FeedSortKey.RECENT);
        SortOption.SortDir dir = sortOption.sortDirOrDefault();
        OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(key, dir);

        long totalCount = fetchTotalCount(first);

        if (totalCount == 0L) {
            return new PageImpl<>(List.of(), pageable, 0L);
        }

        List<Long> pageIds = fetchPageIds(first, orderSpecifiers, pageable);
        if (pageIds.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, totalCount);
        }

        List<Feed> feeds = queryFactory
                .selectFrom(feed).distinct()
                .leftJoin(feed.categories, feedCategoryMapping).fetchJoin()
                .where(feed.id.in(pageIds))
                .fetch();


        Map<Long, Integer> order = new HashMap<>();
        for (int i = 0; i < pageIds.size(); i++)
            order.put(pageIds.get(i), i);

        feeds.sort(Comparator.comparingInt(
                f -> order.getOrDefault(f.getId(), Integer.MAX_VALUE)
        ));

        return new PageImpl<>(feeds, pageable, totalCount);
    }

    @Override
    public Slice<Feed> findByFirstCategoryOrderByCreatedTimeDesc(Long first, Pageable pageable) {
        FeedSearchReqDto req = new FeedSearchReqDto(
                first,
                new SortOption<>(FeedSortKey.RECENT, SortOption.SortDir.DESC)
        );
        return getFeedList(req, pageable);
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
                        feed.createdTime.goe(s),
                        feed.createdTime.lt(e))
                .where(feed.member.id.eq(mId))
                .groupBy(feed.id, feed.topic)
                .orderBy(likeCnt.desc(), feed.id.asc())
                .limit(3)
                .fetch();
    }

    private List<Long> fetchPageIds(Long first, OrderSpecifier<?>[] orderSpecifiers, Pageable pageable) {

        if (first == null) {
            return queryFactory
                    .select(feed.id)
                    .from(feed)
                    .orderBy(orderSpecifiers)
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();
        }

        return queryFactory
                .select(feed.id).distinct()
                .from(feed)
                .join(feed.categories, feedCategoryMapping)
                .where(feedCategoryMapping.firstCategory.id.eq(first))
                .orderBy(orderSpecifiers)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private OrderSpecifier<?>[] buildOrderSpecifiers(FeedSortKey key, SortOption.SortDir dir) {
        Order o = (dir == SortOption.SortDir.ASC) ? Order.ASC : Order.DESC;

        OrderSpecifier<?> tie1 = new OrderSpecifier<>(Order.DESC, feed.createdTime);
        OrderSpecifier<?> tie2 = new OrderSpecifier<>(Order.DESC, feed.id);

        return switch (key) {
            case RECENT -> new OrderSpecifier<?>[] {
                    new OrderSpecifier<>(o, feed.createdTime),
                    tie2
            };

            case VIEWS -> new OrderSpecifier<?>[] {
                    new OrderSpecifier<>(o, feed.viewCount),
                    tie1,
                    tie2
            };
        };
    }

    private long fetchTotalCount(Long first) {
        if (first == null) {
            Long total = queryFactory.select(feed.count()).from(feed).fetchOne();
            return total == null ? 0L : total;
        }

        Long total = queryFactory
                .select(feed.id.countDistinct())
                .from(feed)
                .join(feed.categories, feedCategoryMapping)
                .where(feedCategoryMapping.firstCategory.id.eq(first))
                .fetchOne();
        return total == null ? 0L : total;
    }
}

