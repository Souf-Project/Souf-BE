package com.souf.soufwebsite.domain.member.dto.reqDto.admin;

import java.util.List;

public record BulkRestoreReqDto(
        List<Long> ids
) {}