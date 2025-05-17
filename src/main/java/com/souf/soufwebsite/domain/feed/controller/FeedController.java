package com.souf.soufwebsite.domain.feed.controller;

import com.souf.soufwebsite.domain.feed.dto.FeedCreateResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
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

import static com.souf.soufwebsite.domain.feed.controller.FeedSuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;

    @PostMapping
    public SuccessResponse<FeedCreateResDto> createFeed(
            @RequestBody @Valid FeedReqDto feedReqDto) {
        FeedCreateResDto feedCreateResDto = feedService.createFeed(feedReqDto);

        return new SuccessResponse<>(feedCreateResDto, FEED_CREATE.getMessage());
    }

    @GetMapping
    public SuccessResponse<List<FeedResDto>> getFeeds() {
        return new SuccessResponse<>(feedService.getFeeds(), FEED_GET.getMessage());
    }

    @GetMapping("/{feedId}")
    public SuccessResponse<FeedResDto> getFeed(@PathVariable(name = "feedId") Long feedId) {
        return new SuccessResponse<>(feedService.getFeedById(feedId), FEED_GET.getMessage());
    }

    @PatchMapping("/{feedId}")
    public SuccessResponse<?> updateFeed(
            @PathVariable Long feedId,
            @RequestPart("data") @Valid FeedUpdateReqDto reqDto,
            @RequestPart(name = "files", required = false) List<MultipartFile> newFiles) throws IOException {
        feedService.updateFeed(feedId, reqDto, newFiles);
        return new SuccessResponse<>(FEED_UPDATE.getMessage());
    }

    @DeleteMapping("/{feedId}")
    public SuccessResponse<?> deleteFeed(@PathVariable(name = "feedId") Long feedId) {
        feedService.deleteFeed(feedId);
        return new SuccessResponse<>(FEED_DELETE.getMessage());
    }

}
