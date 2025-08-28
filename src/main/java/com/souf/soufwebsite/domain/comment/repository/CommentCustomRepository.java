package com.souf.soufwebsite.domain.comment.repository;


import com.souf.soufwebsite.domain.feed.entity.Feed;

public interface CommentCustomRepository {

    Long nextCommentGroup(Feed feed);
}
