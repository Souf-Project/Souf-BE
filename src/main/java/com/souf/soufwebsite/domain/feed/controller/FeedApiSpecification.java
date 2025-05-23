package com.souf.soufwebsite.domain.feed.controller;

import com.souf.soufwebsite.domain.feed.dto.FeedDetailResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedReqDto;
import com.souf.soufwebsite.domain.feed.dto.FeedResDto;
import com.souf.soufwebsite.domain.feed.dto.FeedSimpleResDto;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

public interface FeedApiSpecification {

    @Tag(name = "Feed", description = "피드 관련 API")
    @Operation(summary = "피드 생성", description = "학생 권한을 가진 사용자가 피드를 생성합니다.")
    @PostMapping
    SuccessResponse<FeedResDto> createFeed(
            @RequestBody @Valid FeedReqDto feedReqDto);

    @Tag(name = "Feed", description = "피드 관련 API")
    @Operation(summary = "해당 피드 관련 미디어 파일 정보 저장", description = "제공된 presignedUrl을 통해 업로드한 파일의 정보를 DB에도 반영할 수 있도록 서버에게 파일 정보를 보냅니다.")
    @PostMapping("/upload")
    SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto);

    @Tag(name = "Feed", description = "피드 관련 API")
    @Operation(summary = "피드 리스트 조회", description = "특정 학생이 올린 피드 리스트들을 조회합니다.")
    @GetMapping("/{memberId}")
    SuccessResponse<Page<FeedSimpleResDto>> getFeeds(
            @PathVariable(name = "memberId") Long memberId,
            @PageableDefault(size = 12) Pageable pageable);

    @Tag(name = "Feed", description = "학생 피드 관련 API")
    @Operation(summary = "특정 피드 상세 조회", description = "특정 피드에 대한 상세 정보를 조회합니다.")
    @GetMapping("/{memberId}/{feedId}")
    SuccessResponse<FeedDetailResDto> getFeed(
            @PathVariable(name = "memberId") Long memberId,
            @PathVariable(name = "feedId") Long feedId);

    @Tag(name = "Feed", description = "학생 피드 관련 API")
    @Operation(summary = "특정 피드 수정", description = "사용자 본인이 소유한 피드에 대해 수정합니다.")
    @PatchMapping("/{feedId}")
    SuccessResponse<FeedResDto> updateFeed(
            @PathVariable(name = "feedId") Long feedId,
            @RequestBody @Valid FeedReqDto reqDto);

    @Tag(name = "Feed", description = "학생 피드 관련 API")
    @Operation(summary = "특정 피드 삭제", description = "사용자 본인이 소유한 피드를 삭제합니다.")
    @DeleteMapping("/{feedId}")
    SuccessResponse<?> deleteFeed(@PathVariable(name = "feedId") Long feedId);


}
