package com.souf.soufwebsite.domain.file.dto;

import com.souf.soufwebsite.domain.file.entity.File;

import java.util.List;

public record FileReqDto(
        Long postId,
        List<String> fileUrl,
        List<String> fileName,
        List<String> fileType // IMAGE, VIDEO 등
) {
}
