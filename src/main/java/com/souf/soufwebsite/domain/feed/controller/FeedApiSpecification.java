package com.souf.soufwebsite.domain.feed.controller;

import com.souf.soufwebsite.domain.feed.dto.*;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Feed", description = "피드 관련 API")
public interface FeedApiSpecification {

    @Operation(summary = "피드 생성", description = "학생 권한을 가진 사용자가 피드를 생성합니다.")
    @PostMapping
    SuccessResponse<FeedResDto> createFeed(
            @RequestBody @Valid FeedReqDto feedReqDto);

    @Operation(summary = "해당 피드 관련 미디어 파일 정보 저장", description = "제공된 presignedUrl을 통해 업로드한 파일의 정보를 DB에도 반영할 수 있도록 서버에게 파일 정보를 보냅니다.")
    @PostMapping("/upload")
    SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto);

    @Operation(summary = "피드 리스트 조회", description = "특정 학생이 올린 피드 리스트들을 조회합니다.")
    @GetMapping("/{memberId}")
    SuccessResponse<MemberFeedResDto> getStudentFeeds(
            @PathVariable(name = "memberId") Long memberId,
            @PageableDefault(size = 12) Pageable pageable);

    @Operation(summary = "특정 피드 상세 조회", description = "특정 피드에 대한 상세 정보를 조회합니다.")
    @GetMapping("/{memberId}/{feedId}")
    SuccessResponse<FeedDetailResDto> getDetailedFeed(
            @PathVariable(name = "memberId") Long memberId,
            @PathVariable(name = "feedId") Long feedId);

    @Operation(summary = "특정 피드 수정", description = "사용자 본인이 소유한 피드에 대해 수정합니다.")
    @PatchMapping("/{feedId}")
    SuccessResponse<FeedResDto> updateFeed(
            @PathVariable(name = "feedId") Long feedId,
            @RequestBody @Valid FeedReqDto reqDto);

    @Operation(summary = "특정 피드 삭제", description = "사용자 본인이 소유한 피드를 삭제합니다.")
    @DeleteMapping("/{feedId}")
    SuccessResponse<?> deleteFeed(@PathVariable(name = "feedId") Long feedId);

    @Operation(summary = "인기있는 피드 조회", description = "인기있는 피드를 조회합니다.")
    @GetMapping("/popular")
    SuccessResponse<List<FeedSimpleResDto>> getPopularFeeds(
            @PageableDefault(size = 6) Pageable pageable);

    @Operation(summary = "대학생 피드 조회", description = "피드들을 조회합니다.")
    @GetMapping
    SuccessResponse<Slice<FeedDetailResDto>> getFeedList(
            @RequestParam(name = "firstCategory") Long first,
            @PageableDefault(size = 12) Pageable pageable
    );

    @Operation(summary = "피드 좋아요 누르기", description = "잘 작성했다고 생각하는 게시글에 좋아요를 생성/삭제합니다.")
    @PatchMapping("/{feedId}/like")
    SuccessResponse<?> likeFeed(
            @PathVariable(name = "feedId") Long feedId,
            @RequestBody LikeFeedReqDto likeFeedReqDto
    );
}
