package com.souf.soufwebsite.domain.feed.service;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedUpdateReqDto;
import com.souf.soufwebsite.domain.file.dto.FileReqDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FeedService {

    FeedCreateResDto createFeed(FeedReqDto reqDto);

    void uploadFeedMedia(FileReqDto fileReqDto);

    List<FeedResDto> getFeeds();

    FeedResDto getFeedById(Long feedId);

    void updateFeed(Long feedId, FeedUpdateReqDto reqDto, List<MultipartFile> newFiles) throws IOException;

    void deleteFeed(Long feedId);
}
