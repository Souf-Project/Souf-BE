package com.souf.soufwebsite.domain.file.dto;

public record PresignedUrlResDto(
        String presignedUrl,
        String fileUrl,
        String contentType
) {

}
