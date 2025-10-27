package com.souf.soufwebsite.domain.file.event;

import com.souf.soufwebsite.global.common.PostType;

public record MediaCleanupUrlsEvent(
        PostType postType,
        Long postId,
        java.util.List<String> urls
) {}