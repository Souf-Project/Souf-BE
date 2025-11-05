package com.souf.soufwebsite.domain.member.dto.reqDto.addInfo;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.AddressReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record AddCompanyInfoReqDto(

        @Schema(description = "전화번호", example = "010-1111-1111")
        @Pattern(
                regexp = "^010[\\s.-]?\\d{3,4}[\\s.-]?\\d{4}$",
                message = "휴대폰 번호 형식이 올바르지 않습니다. 예) 010-1234-5678"
        )
        @NotBlank
        String phoneNumber,

        @Schema(description = "사업체 유무")
        @NotNull
        Boolean isCompany,

        @Schema(description = "회사명을 입력해주세요.")
        String companyName,

        @Schema(description = "사업자 등록 번호를 입력해주세요.")
        String businessRegistrationNumber,

        @Schema(description = "우편 번호 및 회사 주소")
        AddressReqDto addressReqDto,

        @Schema(description = "업태를 입력해주세요.")
        String businessStatus,

        @Schema(description = "사업자 구분을 입력해주세요.")
        String businessClassification,

        @Schema(description = "사업자 등록증 파일입니다.")
        String businessRegistrationFile
) {
}
