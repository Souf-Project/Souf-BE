package com.souf.soufwebsite.domain.feed.controller;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedUpdateReqDto;
import com.souf.soufwebsite.domain.feed.service.FeedService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public SuccessResponse<?> createFeed(
            @RequestPart("data") @Valid FeedCreateReqDto reqDto,
            @RequestPart("files") List<MultipartFile> files) {
        feedService.createFeed(reqDto, files);

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
    public SuccessResponse<?> updateFeed(
            @PathVariable Long feedId,
            @RequestPart("data") @Valid FeedUpdateReqDto reqDto,
            @RequestPart(name = "files", required = false) List<MultipartFile> newFiles) throws IOException {
        feedService.updateFeed(feedId, reqDto, newFiles);
        return new SuccessResponse<>("Feed updated successfully");
    }

    @DeleteMapping("/{feedId}")
    public SuccessResponse<?> deleteFeed(@PathVariable(name = "feedId") Long feedId) {
        feedService.deleteFeed(feedId);
        return new SuccessResponse<>("Feed deleted successfully");
    }

}
