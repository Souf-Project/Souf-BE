package com.souf.soufwebsite.domain.feed.dto;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FeedCreateReqDto(
        String content,

        @NotEmpty
        List<MultipartFile> files
) {
}
