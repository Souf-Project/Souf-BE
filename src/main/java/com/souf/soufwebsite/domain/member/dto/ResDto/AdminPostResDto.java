package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.file.entity.PostType;

public record AdminPostResDto(
        PostType type,
        String writer,
        String title,
        String createdDate
) {
}
