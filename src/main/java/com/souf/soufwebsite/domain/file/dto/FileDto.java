package com.souf.soufwebsite.domain.file.dto;

import com.souf.soufwebsite.domain.file.entity.File;

public record FileDto(
        Long fileId,
        String fileUrl,
        String fileName,
        String fileType // IMAGE, VIDEO 등
) {
    public static FileDto from(File file) {
        return new FileDto(
                file.getId(),
                file.getFileUrl(),
                file.getFileName(),
                file.getFileType().name()
        );
    }
}
