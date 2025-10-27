package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.souf.soufwebsite.domain.member.entity.MajorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record MajorReqDto(
        @NotNull
        @Schema(description = "전공명은 필수입니다.")
        String specialtyName,

        @NotNull
        @Schema(description = "전공 유형은 필수입니다.")
        MajorType specialty
) {
}
