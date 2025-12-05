package com.souf.soufwebsite.domain.recruit.contract.dto.res;

import com.souf.soufwebsite.domain.file.dto.MediaResDto;

public record SignedContractResDto(
        String contractUuid,
        String projectName,
        MediaResDto resDto
) {
}
