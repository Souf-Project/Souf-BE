package com.souf.soufwebsite.domain.file.dto;

import java.util.List;

public record MediaReqDto(
        Long postId,
        List<String> fileUrl,
        List<String> fileName,
        List<String> fileType // IMAGE, VIDEO 등
) {
}
