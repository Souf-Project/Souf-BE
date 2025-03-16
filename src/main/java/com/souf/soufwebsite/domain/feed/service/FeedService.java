package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;

import java.util.List;

public interface FeedService {

    void createFeed(FeedReqDto reqDto);

    List<FeedResDto> getFeeds();

    FeedResDto getFeedById(Long feedId);

    void updateFeed(Long feedId, FeedReqDto reqDto);

    void deleteFeed(Long feedId);
}
