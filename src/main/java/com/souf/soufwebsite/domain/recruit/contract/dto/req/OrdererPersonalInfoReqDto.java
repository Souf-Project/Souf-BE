package com.souf.soufwebsite.domain.recruit.contract.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OrdererPersonalInfoReqDto(

        @Schema(description = "회사명을 입력해주세요. 회사가 아니라면 빈 문자열을 보내주세요.", example = "철수컴퍼")
        @NotNull(message = "회사가 아니라면 회사명에 빈 문자열을 보내주세요.")
        String companyName,

        @Schema(description = "대표자(회사가 없다면 의뢰자) 성명을 입력해주세요.", example = "김철수")
        @NotBlank(message = "외주 최종 담당자를 반드시 입력해주세요.")
        String ceoName,

        @Schema(description = "사업자 등록번호를 입력해주세요. 회사가 아니라면 빈 문자열을 보내주세요.", example = "111-11-11111")
        @NotNull(message = "회사가 아니라면 사업자 등록번호에 빈 문자열을 보내주세요.")
        String businessRegistrationNumber,

        @Schema(description = "회사 주소를 입력해주세요. 입력하지 않았다면 빈 문자열을 보내주세요.", example = "서울시 가나구 다라동 140-5")
        @NotNull(message = "입력하지 않았다면 빈 문자열을 보내주세요.")
        String roadNameAddress,

        @Schema(description = "회사 전화번호를 입력해주세요.", example = "02-111-1111")
        @NotBlank(message = "전화번호는 필수입니다.")
        String companyPhoneNumber,

        @Schema(description = "외주 진행 중 연락할 이메일을 기입해주세요.", example = "swaggerEmail11@naver.com")
        @NotBlank(message = "외주 진행 중 연락 이메일은 필수입니다.")
        String contactEmail,

        @Schema(description = "담당자 성명/직책을 입력해주세요.", example = "김훈이/PM")
        @NotNull(message = "회사일 경우, 담당자 성명/직책은 필수입니다. 아니라면 빈 문자열을 보내주세요.")
        String managerWithPosition
) {
}
