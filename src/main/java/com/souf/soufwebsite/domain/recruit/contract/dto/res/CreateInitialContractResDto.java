package com.souf.soufwebsite.domain.recruit.contract.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateInitialContractResDto(

        @Schema(description = "계약서 uuid입니다.")
        String contractUuid,
        @Schema(description = "수급자가 조회/작성 할 수 있는 초대 코드입니다.")
        String inviteToken
) {
}
