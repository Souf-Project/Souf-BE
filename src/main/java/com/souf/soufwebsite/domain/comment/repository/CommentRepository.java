package com.souf.soufwebsite.domain.comment.repository;

import com.souf.soufwebsite.domain.comment.entity.Comment;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Query(
            value = """
    SELECT c
    FROM Comment c
      JOIN FETCH c.writer w
    WHERE c.feed.id = :postId
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

    @Query(
            value = """
                SELECT c
                FROM Comment c
                JOIN FETCH c.writer w
                WHERE c.feed.id = :feedId
                AND c.commentGroup = :commentGroup
                AND c.id <> c.commentGroup
                ORDER BY c.createdTime ASC\s
           \s""",
            countQuery = """
                SELECT COUNT(c)
                FROM Comment c
                WHERE c.feed.id = :feedId
                    AND c.commentGroup = :commentGroup
                    AND c.id <> c.commentGroup
            """
    )
    Page<Comment> findRepliesByFeedIdAndGroup(@Param("feedId") Long feedId, @Param("commentGroup") Long commentGroup, Pageable pageable);

    Optional<Long> countByFeed(Feed feed);

    List<Comment> findByFeedIdAndCommentGroup(Long feedId, Long commentGroup);
}
