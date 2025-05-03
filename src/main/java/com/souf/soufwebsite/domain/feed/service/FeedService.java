package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedUpdateReqDto;

import java.util.List;

public interface FeedService {

    void createFeed(FeedCreateReqDto reqDto);

    List<FeedResDto> getFeeds();

    FeedResDto getFeedById(Long feedId);

    void updateFeed(Long feedId, FeedUpdateReqDto reqDto);

    void deleteFeed(Long feedId);
}
