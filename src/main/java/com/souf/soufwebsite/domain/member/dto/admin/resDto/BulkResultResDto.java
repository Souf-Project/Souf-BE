package com.souf.soufwebsite.domain.member.dto.admin.resDto;

import java.util.List;

public record BulkResultResDto(
        int requested,
        int processed,
        List<Long> notFound,
        List<Long> alreadyProcessed,
        List<FailedItem> failed
) {
    public record FailedItem(Long id, String code) {}
}
