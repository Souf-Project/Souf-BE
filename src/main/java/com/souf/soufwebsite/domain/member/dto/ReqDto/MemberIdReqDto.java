package com.souf.soufwebsite.domain.member.dto.ReqDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MemberIdReqDto(
        @NotNull
        @Schema(description = "회원 아이디를 보내주세요.")
        Long memberId
) {
}
