package com.souf.soufwebsite.domain.member.dto.admin.reqDto;

import java.util.List;

public record BulkRestoreReqDto(
        List<Long> ids
) {}