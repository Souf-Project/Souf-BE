package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FeedService {

    FeedCreateResDto createFeed(FeedReqDto reqDto);

    void uploadFeedMedia(MediaReqDto mediaReqDto);

    Page<FeedSimpleResDto> getFeeds(Long memberId, Pageable pageable);

    FeedResDto getFeedById(Long memberId, Long feedId);

    void updateFeed(Long feedId, FeedUpdateReqDto reqDto, List<MultipartFile> newFiles) throws IOException;

    void deleteFeed(Long feedId);
}
