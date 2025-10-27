package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ClubSignupReqDto(
        @JsonUnwrapped CommonSignupReqDto common,

        @Schema(description = "동아리 인증 수단을 기입해주세요.")
        String clubAuthenticationMethod,

        @Schema(description = "동아리 정보를 기입해주세요.")
        String introduction
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

