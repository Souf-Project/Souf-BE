package com.souf.soufwebsite.domain.file.dto.video;

public record S3UploadPartsDetailDto(
        int partNumber,
        String awsETag
) {

}
