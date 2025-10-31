package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;

import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "roleType",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = StudentSignupReqDto.class, name = "STUDENT"),
        @JsonSubTypes.Type(value = CompanySignupReqDto.class, name = "MEMBER"),
        @JsonSubTypes.Type(value = ClubSignupReqDto.class, name = "CLUB")
})
public sealed interface SignupReqDto permits StudentSignupReqDto, CompanySignupReqDto, ClubSignupReqDto {
    RoleType roleType();

    String email();

    String password();

    String passwordCheck();

    String username();

    String nickname();

    String phoneNumber();

    List<CategoryDto> categoryDtos();

    Boolean isSuitableAged();

    Boolean isPersonalInfoAgreed();

    Boolean isServiceUtilizationAgreed();

    Boolean isMarketingAgreed();
}