package com.souf.soufwebsite.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.souf.soufwebsite.domain.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long nextCommentGroup(Feed feed) {
        Long max = queryFactory
                .select(comment.commentGroup.max())
                .from(comment)
                .where(comment.feed.eq(feed))
                .fetchOne();
        return (max == null) ? 1L : max + 1;
    }
}
