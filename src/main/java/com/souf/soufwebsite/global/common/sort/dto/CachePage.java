package com.souf.soufwebsite.global.common.sort.dto;

import java.util.List;

public record CachePage<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        String sort
) {
}
