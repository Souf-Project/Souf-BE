package com.souf.soufwebsite.domain.member.dto.ReqDto.signup;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ClubSignupReqDto(
        @JsonUnwrapped CommonSignupReqDto signupReqDto,

        @Schema(description = "동아리 인증 수단을 기입해주세요.")
        String clubAuthenticationMethod,

        @Schema(description = "동아리 정보를 기입해주세요.")
        String introduction
) implements SignupReqDto {

    @Override
    public RoleType roleType() {
        return signupReqDto.roleType();
    }

    @Override
    public String email() {
        return signupReqDto.email();
    }

    @Override
    public String password() {
        return signupReqDto.password();
    }

    @Override
    public String passwordCheck() {
        return signupReqDto.passwordCheck();
    }

    @Override
    public String username() {
        return signupReqDto.username();
    }

    @Override
    public String nickname() {
        return signupReqDto().nickname();
    }

    @Override
    public List<CategoryDto> categoryDtos() {
        return signupReqDto().categoryDtos();
    }

    @Override
    public Boolean isSuitableAged() {
        return signupReqDto().isSuitableAged();
    }

    @Override
    public Boolean isPersonalInfoAgreed() {
        return signupReqDto().isPersonalInfoAgreed();
    }

    @Override
    public Boolean isServiceTermsAgreed() {
        return signupReqDto().isServiceTermsAgreed();
    }

    @Override
    public Boolean isMarketingAgreed() {
        return signupReqDto().isMarketingAgreed();
    }
}
