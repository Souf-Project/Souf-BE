package com.souf.soufwebsite.domain.file.dto;

public record S3UploadPartsDetailDto(
        int partNumber,
        String awsTag
) {

}
