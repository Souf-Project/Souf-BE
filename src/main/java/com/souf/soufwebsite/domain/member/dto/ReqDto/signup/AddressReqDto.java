package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddressReqDto(

        @Schema(description = "우편번호는 필수입니다.")
        @NotBlank
        String zipCode,

        @Schema(description = "도로명 주소는 필수입니다.")
        @NotBlank
        String roadNameAddress,

        @Schema(description = "상세 주소가 없다면 빈 문자열을 보내주세요.")
        @NotNull(message = "없다면 빈 문자열을 보내주세요")
        String detailedAddress
) {
}
