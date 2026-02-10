package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.req.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.req.FeedSearchReqDto;
import com.souf.soufwebsite.domain.feed.dto.req.LikeFeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.res.*;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FeedService {

    FeedCreatedResDto createFeed(String email, FeedReqDto reqDto);

    void uploadFeedMedia(MediaReqDto mediaReqDto);

    MemberFeedResDto getStudentFeeds(Long memberId, Pageable pageable);

    FeedDetailResDto getFeedById(Long memberId, Long feedId, String ip, String userAgent);

    FeedCreatedResDto updateFeed(String email, Long feedId, FeedReqDto reqDto);

    void deleteFeed(String email, Long feedId);

    List<PopularFeedResDto> getPopularFeeds();

    Page<FeedSimpleResDto> getFeeds(FeedSearchReqDto reqDto, Pageable pageable);

    void updateLikedCount(Long feedId, LikeFeedReqDto likeFeedReqDto);
}
