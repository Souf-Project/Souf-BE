package com.souf.soufwebsite.domain.comment.repository;

import com.souf.soufwebsite.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    @Query(
            value = """
        SELECT c
          FROM Comment c
         WHERE c.postId = :postId
           AND c.id IN (
                 SELECT MIN(c2.id)
                   FROM Comment c2
                  WHERE c2.postId = :postId
                  GROUP BY c2.commentGroup
               )
      ORDER BY c.commentGroup ASC
      """,
            countQuery = """
        SELECT COUNT(DISTINCT c2.commentGroup)
          FROM Comment c2
         WHERE c2.postId = :postId
      """
    )
    Slice<Comment> findFirstByGroup(
            @Param("postId") Long postId,
            Pageable pageable
    );

    Page<Comment> findByPostIdAndCommentGroupOrderByCreatedTime(Long postId, Long commentGroup, Pageable pageable);
}
