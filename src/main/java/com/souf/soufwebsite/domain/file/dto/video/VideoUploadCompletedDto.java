package com.souf.soufwebsite.domain.file.dto.video;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record VideoUploadCompletedDto(

        String uploadId,
        String fileUrl,
        List<S3UploadPartsDetailDto> parts,

        @Schema(description = "게시글 타입을 적어주세요", example = "feed, char, recruit")
        String type
) {
}
