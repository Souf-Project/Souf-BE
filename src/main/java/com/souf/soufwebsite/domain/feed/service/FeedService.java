package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface FeedService {

    FeedResDto createFeed(FeedReqDto reqDto);

    void uploadFeedMedia(MediaReqDto mediaReqDto);

    Page<FeedSimpleResDto> getFeeds(Long memberId, Pageable pageable);

    FeedDetailResDto getFeedById(Long memberId, Long feedId);

    FeedResDto updateFeed(Long feedId, FeedReqDto reqDto);

    void deleteFeed(Long feedId);
}
