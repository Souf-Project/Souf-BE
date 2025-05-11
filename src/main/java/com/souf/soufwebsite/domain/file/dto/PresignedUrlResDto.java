package com.souf.soufwebsite.domain.file.dto;

import java.util.List;

public record PresignedUrlResDto(
        String presignedUrl,
        String fileUrl
) {

}
