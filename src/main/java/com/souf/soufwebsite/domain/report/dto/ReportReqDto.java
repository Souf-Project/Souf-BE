package com.souf.soufwebsite.domain.report.dto;

import com.souf.soufwebsite.domain.file.entity.PostType;

public record ReportReqDto(

        PostType postType,
        Long postId,
        String title,

        Long reporterId,
        Long reportedMemberId,

        Long reasonId,
        String description
) {
}
