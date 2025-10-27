package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CompanySignupReqDto(
        @JsonUnwrapped CommonSignupReqDto common,

        @Schema(description = "회사명")
        String companyName,

        @Schema(description = "사업자 등록 번호를 입력해주세요.")
        String businessRegistrationNumber,

        @Schema(description = "우편 번호 및 회사 주소")
        AddressReqDto addressReqDto,

        @Schema(description = "업태")
        String businessStatus,

        @Schema(description = "사업자 구분")
        String businessClassification,

        @Schema(description = "사업자 등록증 파일입니다.")
        String businessRegistrationFile

) implements SignupReqDto {

    @Override
    public RoleType roleType() {
        return common.roleType();
    }

    @Override
    public String email() {
        return common.email();
    }

    @Override
    public String password() {
        return common.password();
    }

    @Override
    public String passwordCheck() {
        return common.passwordCheck();
    }

    @Override
    public String username() {
        return common.username();
    }

    @Override
    public String nickname() {
        return common.nickname();
    }

    @Override
    public List<CategoryDto> categoryDtos() {
        return common.categoryDtos();
    }

    @Override
    public Boolean isSuitableAged() {
        return common.isSuitableAged();
    }

    @Override
    public Boolean isPersonalInfoAgreed() {
        return common.isPersonalInfoAgreed();
    }

    @Override
    public Boolean isServiceUtilizationAgreed() {
        return common.isServiceUtilizationAgreed();
    }

    @Override
    public Boolean isMarketingAgreed() {
        return common.isMarketingAgreed();
    }
}

