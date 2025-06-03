package com.souf.soufwebsite.domain.feed.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.souf.soufwebsite.domain.feed.entity.QFeed.feed;
import static com.souf.soufwebsite.domain.feed.entity.QFeedCategoryMapping.feedCategoryMapping;

@Repository
@RequiredArgsConstructor
public class FeedCustomRepositoryImpl implements FeedCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Slice<Feed> findByFirstCategoryOrderByCreatedTimeDesc(Long first, Pageable pageable) {
        List<Feed> feeds = queryFactory
                .selectFrom(feed)
                .join(feed.categories, feedCategoryMapping)
                .where(first != null ? feedCategoryMapping.firstCategory.id.eq(first) : null)
                .orderBy(feed.createdTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (feeds.size() > pageable.getPageSize()) {
            feeds.remove(pageable.getPageSize());
            hasNext = true;
        }

        return new SliceImpl<>(feeds, pageable, hasNext);
    }
}

