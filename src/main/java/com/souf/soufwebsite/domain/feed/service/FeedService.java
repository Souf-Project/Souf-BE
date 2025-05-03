package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedUpdateReqDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FeedService {

    void createFeed(FeedCreateReqDto reqDto, List<MultipartFile> files);

    List<FeedResDto> getFeeds();

    FeedResDto getFeedById(Long feedId);

    void updateFeed(Long feedId, FeedUpdateReqDto reqDto, List<MultipartFile> newFiles) throws IOException;

    void deleteFeed(Long feedId);
}
