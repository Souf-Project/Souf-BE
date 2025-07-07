package com.souf.soufwebsite.domain.file.dto.video;

public record S3VideoUploadSignedUrlReqDto(
        String uploadId,
        int partNumber,
        String fileName
) {
}
