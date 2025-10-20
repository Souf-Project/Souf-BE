package com.souf.soufwebsite.domain.member.dto.ReqDto;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record SignupReqDto(

        @Schema(description = "회원 구분 ", example = "STUDENT")
        @NotNull RoleType roleType,

        @Schema(description = "사용자 살명", example = "홍길동")
        @NotEmpty String username,

        @Schema(description = "닉네임", example = "김개똥")
        @NotEmpty String nickname,

        @Schema(description = "이메일", example = "user@example.com")
        @NotEmpty @Email String email,

        @Schema(description = "비밀번호", example = "Passw0rd!")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
                message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
        )
        @NotEmpty String password,

        @Schema(description = "비밀번호 확인", example = "Passw0rd!")
        @NotEmpty String passwordCheck,

        @Schema(description = "카테고리 목록", example = "[{\"firstCategory\": 1, \"secondCategory\": 1, \"thirdCategory\": 1}]")
        List<CategoryDto> categoryDtos,

        @NotNull
        @Schema(description = "개인정보 동의서 확인 여부", example = "true여야 합니다.")
        Boolean isPersonalInfoAgreed,

        @Schema(description = "마케팅 동의 확인 여부", example = "true or false")
        Boolean isMarketingAgreed

) {
        @AssertTrue(message = "학생(STUDENT) 가입은 .ac.kr 이메일만 가능합니다.")
        public boolean isStudentEmailValid() {
                if (roleType != RoleType.STUDENT) return true;
                if (email == null) return false;
                return email.toLowerCase().endsWith(".ac.kr");
        }
}