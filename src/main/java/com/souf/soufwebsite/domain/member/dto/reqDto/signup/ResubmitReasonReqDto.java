package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ResubmitReasonReqDto(

        @Schema(description = "재제출을 요구하는 이유를 메일에 표기합니다.")
        @NotNull(message = "재제출 사유를 적어주세요.")
        String reason
) {
}
