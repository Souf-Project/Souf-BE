package com.souf.soufwebsite.domain.file.dto;

public record S3VideoUploadSignedUrlReqDto(
        String uploadId,
        int partNumber,
        String fileName
) {
}
