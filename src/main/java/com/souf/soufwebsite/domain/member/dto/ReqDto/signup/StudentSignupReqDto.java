package com.souf.soufwebsite.domain.member.dto.ReqDto.signup;

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
        @JsonUnwrapped CommonSignupReqDto signupReqDto,

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
