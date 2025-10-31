package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.util.List;

@Getter
@JsonTypeName("CLUB")
public final class ClubSignupReqDto implements SignupReqDto {

    @Valid @JsonUnwrapped CommonSignupReqDto common;

    @Schema(description = "동아리 인증 수단을 기입해주세요.")
    String clubAuthenticationMethod;

    @Size(max = 300)
    @Schema(description = "동아리 정보를 기입해주세요. 100자 이내")
    String intro;

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
    public String phoneNumber() { return common.phoneNumber(); }

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

