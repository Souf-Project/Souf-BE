package com.souf.soufwebsite.domain.feed.repository;

import com.souf.soufwebsite.domain.feed.competition.dto.CompetitionFeedRowResDto;
import com.souf.soufwebsite.domain.feed.dto.req.FeedSearchReqDto;
import com.souf.soufwebsite.domain.feed.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public interface FeedCustomRepository{

    Page<Feed> getFeedList(FeedSearchReqDto req, Pageable pageable);

    Slice<Feed> findByFirstCategoryOrderByCreatedTimeDesc(Long first, Pageable pageable);

    List<CompetitionFeedRowResDto> findTop3ByMemberOrderByFeedLikes(Long memberId, LocalDateTime start, LocalDateTime end);
}
