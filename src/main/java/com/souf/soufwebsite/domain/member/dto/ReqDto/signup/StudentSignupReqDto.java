package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record StudentSignupReqDto(
        @JsonUnwrapped CommonSignupReqDto common,

        @NotBlank
        @Schema(description = "학교명 입력은 필수입니다.", example = "세종대학교")
        String schoolName,

        @NotNull
        @Schema(description = "전공은 최소 하나가 필요합니다.")
        List<MajorReqDto> majorReqDtos,

        @NotEmpty
        @Email
        @Schema(description = "학교 이메일을 입력해주세요.")
        String schoolEmail,

        @NotEmpty
        @Schema(description = "학교 인증 파일은 필수입니다.")
        String schoolAuthenticatedImageFileName

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

