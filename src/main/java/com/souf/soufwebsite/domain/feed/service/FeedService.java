package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FeedService {

    FeedResDto createFeed(FeedReqDto reqDto);

    void uploadFeedMedia(MediaReqDto mediaReqDto);

    MemberFeedResDto getStudentFeeds(Long memberId, Pageable pageable);

    FeedDetailResDto getFeedById(Long memberId, Long feedId);

    FeedResDto updateFeed(Long feedId, FeedReqDto reqDto);

    void deleteFeed(Long feedId);

    Page<FeedSimpleResDto> getPopularFeeds(Pageable pageable);

    Slice<FeedDetailResDto> getFeeds(Long first, Pageable pageable);
}
