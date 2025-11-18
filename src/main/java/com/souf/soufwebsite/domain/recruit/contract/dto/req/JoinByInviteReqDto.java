package com.souf.soufwebsite.domain.recruit.contract.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record JoinByInviteReqDto(

        @Schema(description = "계약서 발주자 작성 API를 호출하고 받은 token을 넣어주세요.")
        @NotBlank(message = "초대 코드는 필수입니다.")
        String inviteToken
) {
}
