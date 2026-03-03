package com.souf.soufwebsite.domain.member.dto.admin.reqDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record ResubmitReasonReqDto(

        @Schema(description = "거절을 한다면 관리자 페이지 내의 멤버 상세조희의 인증서류 url을 보내주세요.", example = "profile/authentication/origin/...")
        String originalUrl,

        @Schema(description = "재제출을 요구하는 이유를 메일에 표기합니다.")
        @NotNull(message = "재제출 사유를 적어주세요.")
        String reason
) {
}
