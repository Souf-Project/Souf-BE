package com.souf.soufwebsite.domain.member.dto.admin.reqDto;

import java.util.List;

public record BulkDeleteReqDto(
        List<Long> ids,
        String reason
) {}
