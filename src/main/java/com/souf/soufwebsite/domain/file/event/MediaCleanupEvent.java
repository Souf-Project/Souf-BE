package com.souf.soufwebsite.domain.file.event;

import com.souf.soufwebsite.global.common.PostType;

public record MediaCleanupEvent(PostType postType, Long postId) {}