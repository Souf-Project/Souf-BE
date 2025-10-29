package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record AddressReqDto(

        @Schema(description = "우편번호는 필수입니다.")
        @NotBlank
        String zipCode,

        @Schema(description = "도로명 주소는 필수입니다.")
        @NotBlank
        String roadNameAddress,

        @Schema(description = "상세 주소는 필수가 아닙니다.")
        String detailedAddress
) {
}
