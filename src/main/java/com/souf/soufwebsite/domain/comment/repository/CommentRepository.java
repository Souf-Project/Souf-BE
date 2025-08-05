package com.souf.soufwebsite.domain.comment.repository;

import com.souf.soufwebsite.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentCustomRepository {

    Slice<Comment> findDistinctByPostIdOrderByCommentGroup(Long postId, Pageable pageable);

    Page<Comment> findByPostIdAndCommentGroupOrderByCreatedTime(Long postId, Long commentGroup, Pageable pageable);
}
