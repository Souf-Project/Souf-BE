package com.souf.soufwebsite.domain.feed.controller;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.feed.service.FeedService;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.souf.soufwebsite.domain.feed.controller.FeedSuccessMessage.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController implements FeedApiSpecification{

    private final FeedService feedService;

    @PostMapping
    public SuccessResponse<FeedResDto> createFeed(
            @CurrentEmail String email,
            @RequestBody @Valid FeedReqDto feedReqDto) {
        FeedResDto feedResDto = feedService.createFeed(email, feedReqDto);

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
            @PathVariable(name = "feedId") Long feedId,
            HttpServletRequest request) {

        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        FeedDetailResDto result = feedService.getFeedById(memberId, feedId, ip, userAgent);

        return new SuccessResponse<>(result, FEED_GET.getMessage());
    }

    @PatchMapping("/{feedId}")
    public SuccessResponse<FeedResDto> updateFeed(
            @CurrentEmail String email,
            @PathVariable(name = "feedId") Long feedId,
            @RequestBody @Valid FeedReqDto reqDto) {
        FeedResDto feedResDto = feedService.updateFeed(email, feedId, reqDto);
        return new SuccessResponse<>(feedResDto, FEED_UPDATE.getMessage());
    }

    @DeleteMapping("/{feedId}")
    public SuccessResponse<?> deleteFeed(
            @CurrentEmail String email,
            @PathVariable(name = "feedId") Long feedId) {
        feedService.deleteFeed(email, feedId);
        return new SuccessResponse<>(FEED_DELETE.getMessage());
    }

    @GetMapping("/popular")
    public SuccessResponse<List<FeedSimpleResDto>> getPopularFeeds(){

        log.info("피드 캐싱 조회");
        return new SuccessResponse<>(feedService.getPopularFeeds(),
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

    @PatchMapping("/{feedId}/like")
    public SuccessResponse<?> likeFeed(
            @PathVariable(name = "feedId") Long feedId,
            @RequestBody LikeFeedReqDto likeFeedReqDto
    ){
        feedService.updateLikedCount(feedId, likeFeedReqDto);
        return new SuccessResponse<>(FEED_LIKE_UPDATE_SUCCESS.getMessage());
    }
}
