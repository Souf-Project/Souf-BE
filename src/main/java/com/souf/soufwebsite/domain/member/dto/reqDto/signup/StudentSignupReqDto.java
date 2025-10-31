package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.entity.profile.EducationType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.util.List;

@Getter
@JsonTypeName("STUDENT")
public final class StudentSignupReqDto implements SignupReqDto {

    @Valid @JsonUnwrapped CommonSignupReqDto common;

    @NotBlank(message = "학교명은 반드시 입력해주세요.")
    @Schema(description = "학교명 입력은 필수입니다.", example = "세종대학교")
    String schoolName;

    @NotNull(message = "대학교인지 대학원인지 반드시 입력해주세요.")
    @Schema(description = "학력은 필수입니다.", example = "UNIV(대학교) or GRADUATE(대학원)")
    EducationType educationType;

    @NotNull(message = "전공은 반드시 입력해주세요.")
    @Schema(description = "전공은 최소 하나가 필요합니다.")
    List<MajorReqDto> majorReqDtos;

    @NotEmpty(message = "이벤트 알림을 받기 위한 학교 이메일은 필수입니다.")
    @Email
    @Schema(description = "학교 이메일을 입력해주세요.")
    String schoolEmail;

    @NotEmpty(message = "학교 인증를 인증하기 위한 파일을 제시해주세요.")
    @Schema(description = "학교 인증 파일은 필수입니다.")
    String schoolAuthenticatedImageFileName;


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

    @AssertTrue(message = "학생(STUDENT) 가입은 .ac.kr 이메일만 가능합니다.")
    public boolean isStudentEmailValid() {
        if (roleType() != RoleType.STUDENT) return true;
        if (email() == null) return false;
        return email().toLowerCase().endsWith(".ac.kr");
    }
}

