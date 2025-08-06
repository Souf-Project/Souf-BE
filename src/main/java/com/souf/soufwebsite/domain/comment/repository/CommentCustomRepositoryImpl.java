package com.souf.soufwebsite.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.souf.soufwebsite.domain.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentCustomRepositoryImpl implements CommentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long nextCommentGroup(Long postId) {
        Long max = queryFactory
                .select(comment.commentGroup.max())
                .from(comment)
                .where(comment.postId.eq(postId))
                .fetchOne();
        return (max == null) ? 1L : max + 1;
    }
}
