package com.souf.soufwebsite.domain.file.dto.video;

import java.util.List;

public record VideoUploadCompletedDto(
        String uploadId,
        String fileUrl,
        List<S3UploadPartsDetailDto> parts,
        String type
) {
}
