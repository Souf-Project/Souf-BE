package com.souf.soufwebsite.domain.file.dto;

import com.souf.soufwebsite.domain.file.entity.File;

public record FileReqDto(
        Long postId,
        String fileUrl,
        String fileName,
        String fileType // IMAGE, VIDEO 등
) {
    public static FileReqDto from(File file) {
        return new FileReqDto(
                file.getId(),
                file.getFileUrl(),
                file.getFileName(),
                file.getFileType().name()
        );
    }
}
