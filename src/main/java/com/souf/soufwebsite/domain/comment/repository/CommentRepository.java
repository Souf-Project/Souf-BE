package com.souf.soufwebsite.domain.comment.repository;

import com.souf.soufwebsite.domain.comment.entity.Comment;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Query(
            value = """
    SELECT c
    FROM Comment c
      JOIN FETCH c.feed f
      JOIN FETCH c.writer w
    WHERE f.id = :postId
      AND c.id = (
        SELECT MIN(c2.id)
        FROM Comment c2
        WHERE c2.feed.id = :postId
          AND c2.commentGroup = c.commentGroup
      )
    ORDER BY c.commentGroup ASC, c.id ASC
  """,
            countQuery = """
    SELECT COUNT(DISTINCT c2.commentGroup)
    FROM Comment c2
    WHERE c2.feed.id = :postId
    """
    )
    Slice<Comment> findFirstByGroup(
            @Param("postId") Long postId,
            Pageable pageable
    );

    // 여기 고치기
    Page<Comment> findByFeedAndCommentGroupOrderByCreatedTime(Feed feed, Long commentGroup, Pageable pageable);

    Optional<Long> countByFeed(Feed feed);
}
