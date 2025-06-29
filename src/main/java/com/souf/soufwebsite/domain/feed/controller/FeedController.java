package com.souf.soufwebsite.domain.feed.controller;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.feed.service.FeedService;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.feed.controller.FeedSuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController implements FeedApiSpecification{

    private final FeedService feedService;

    @PostMapping
    public SuccessResponse<FeedResDto> createFeed(
            @RequestBody @Valid FeedReqDto feedReqDto) {
        FeedResDto feedResDto = feedService.createFeed(feedReqDto);

        return new SuccessResponse<>(feedResDto, FEED_CREATE.getMessage());
    }

    @PostMapping("/upload")
    public SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto){
        feedService.uploadFeedMedia(mediaReqDto);

        return new SuccessResponse(FEED_FILE_METADATA_CREATE.getMessage());
    }

    @GetMapping("/{memberId}")
    public SuccessResponse<MemberFeedResDto> getStudentFeeds(
            @PathVariable(name = "memberId") Long memberId,
            @PageableDefault(size = 12) Pageable pageable) {
        return new SuccessResponse<>(feedService.getStudentFeeds(memberId, pageable), FEED_GET.getMessage());
    }

    @GetMapping("/{memberId}/{feedId}")
    public SuccessResponse<FeedDetailResDto> getDetailedFeed(
            @PathVariable(name = "memberId") Long memberId,
            @PathVariable(name = "feedId") Long feedId) {
        return new SuccessResponse<>(feedService.getFeedById(memberId, feedId), FEED_GET.getMessage());
    }

    @PatchMapping("/{feedId}")
    public SuccessResponse<FeedResDto> updateFeed(
            @PathVariable(name = "feedId") Long feedId,
            @RequestBody @Valid FeedReqDto reqDto) {
        FeedResDto feedResDto = feedService.updateFeed(feedId, reqDto);
        return new SuccessResponse<>(feedResDto, FEED_UPDATE.getMessage());
    }

    @DeleteMapping("/{feedId}")
    public SuccessResponse<?> deleteFeed(@PathVariable(name = "feedId") Long feedId) {
        feedService.deleteFeed(feedId);
        return new SuccessResponse<>(FEED_DELETE.getMessage());
    }

    @GetMapping("/popular")
    public SuccessResponse<Page<FeedSimpleResDto>> getPopularFeeds(
            @PageableDefault(size = 12) Pageable pageable){
        return new SuccessResponse<>(feedService.getPopularFeeds(pageable),
                FEED_GET_POPULATION.getMessage());
    }

    @GetMapping
    public SuccessResponse<Slice<FeedDetailResDto>> getFeedList(
            @RequestParam(name = "firstCategory") Long first,
            @PageableDefault(size = 12) Pageable pageable) {
        return new SuccessResponse<>(
                feedService.getFeeds(first, pageable),
                FEED_GET.getMessage()
        );
    }
}
