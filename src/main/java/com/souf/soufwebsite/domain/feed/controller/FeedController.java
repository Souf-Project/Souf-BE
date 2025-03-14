package com.souf.soufwebsite.domain.feed.controller;

import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.service.FeedService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public SuccessResponse<?> createFeed(FeedReqDto feedReqDto) {
        feedService.createFeed(feedReqDto);

        return new SuccessResponse<>("Feed created successfully");
    }

    @GetMapping
    public SuccessResponse<List<FeedResDto>> getFeeds() {
        return new SuccessResponse<>(feedService.getFeeds());
    }

    @GetMapping("/{feedId}")
    public SuccessResponse<FeedResDto> getFeed(@PathVariable(name = "feedId") Long feedId) {
        return new SuccessResponse<>(feedService.getFeedById(feedId));
    }

    @PatchMapping("/{feedId}")
    public SuccessResponse<?> updateFeed(@PathVariable(name = "feedId") Long feedId, FeedReqDto feedReqDto) {
        feedService.updateFeed(feedId, feedReqDto);
        return new SuccessResponse<>("Feed updated successfully");
    }

    @DeleteMapping("/{feedId}")
    public SuccessResponse<?> deleteFeed(@PathVariable(name = "feedId") Long feedId) {
        feedService.deleteFeed(feedId);
        return new SuccessResponse<>("Feed deleted successfully");
    }

}
