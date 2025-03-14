package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;

import java.util.List;

public interface FeedService {

    void createFeed(FeedReqDto feedReqDto);

    List<FeedResDto> getFeeds();

    FeedResDto getFeedById(Long feedId);

    void updateFeed(Long feedId, FeedReqDto feedReqDto);

    void deleteFeed(Long feedId);
}
