package com.souf.soufwebsite.domain.comment.repository;


public interface CommentCustomRepository {

    Long nextCommentGroup(Long postId);
}
