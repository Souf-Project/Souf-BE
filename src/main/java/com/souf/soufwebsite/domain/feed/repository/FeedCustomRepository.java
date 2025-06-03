package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FeedCustomRepository{

    Slice<Feed> findByFirstCategoryOrderByCreatedTimeDesc(Long first, Pageable pageable);
}
