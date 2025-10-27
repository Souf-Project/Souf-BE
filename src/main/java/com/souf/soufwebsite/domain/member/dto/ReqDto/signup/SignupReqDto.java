package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public sealed interface SignupReqDto permits StudentSignupReqDto, CompanySignupReqDto, ClubSignupReqDto {
    @Schema(description = "회원 구분", example = "STUDENT")
    @NotNull
    RoleType roleType();

    @Schema(description = "이메일", example = "user@example.com")
    @NotEmpty @Email
    String email();

    @Schema(description = "비밀번호", example = "Passw0rd!")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
            message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
    )
    @NotEmpty String password();

    @Schema(description = "비밀번호 확인", example = "Passw0rd!")
    @NotEmpty String passwordCheck();

    @Schema(description = "실명 or 동아리 대표자명", example = "홍길동")
    @NotEmpty String username();

    @Schema(description = "닉네임 or 동아리명", example = "김개똥")
    @NotEmpty
    String nickname();

    @Schema(description = "카테고리 목록",
            example = "[{\"firstCategory\":1,\"secondCategory\":1,\"thirdCategory\":1}]")
    List<CategoryDto> categoryDtos();

    @NotNull
    @Schema(description = "나이 제한 여부", example = "true")
    Boolean isSuitableAged();

    @NotNull
    @Schema(description = "개인정보 동의 여부", example = "true")
    Boolean isPersonalInfoAgreed();

    @NotNull
    @Schema(description = "서비스 이용 약관 동의 여부", example = "true")
    Boolean isServiceUtilizationAgreed();

    @Schema(description = "마케팅 동의 여부", example = "true or false")
    Boolean isMarketingAgreed();

}