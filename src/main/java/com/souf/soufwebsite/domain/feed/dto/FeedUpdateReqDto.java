package com.souf.soufwebsite.domain.feed.dto;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FeedUpdateReqDto(
        String content,

        List<Long> keepFileIds,                      // 기존 파일 중 유지할 id 목록
        List<MultipartFile> newFiles                // 새로 추가할 파일들
) {}
