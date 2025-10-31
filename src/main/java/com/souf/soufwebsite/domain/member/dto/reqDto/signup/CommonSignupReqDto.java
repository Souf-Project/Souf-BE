package com.souf.soufwebsite.domain.member.dto.reqDto.signup;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record CommonSignupReqDto(
        @Schema(description = "회원 구분 ", example = "STUDENT")
        @NotNull RoleType roleType,

        @Schema(description = "이메일", example = "user@example.com")
        @NotEmpty
        @Email
        String email,

        @Schema(description = "비밀번호", example = "Passw0rd!")
        @Size(min = 8, max = 20, message = "비밀번호는 8~20자 사이여야 합니다.")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$",
                message = "비밀번호는 영문자·숫자·특수문자를 모두 포함해야 합니다."
        )
        @NotEmpty
        String password,

        @Schema(description = "비밀번호 확인", example = "Passw0rd!")
        @NotEmpty
        String passwordCheck,

        @Schema(description = "사용자 실명 or 동아리 대표자명", example = "홍길동")
        @NotEmpty
        String username,

        @Schema(description = "닉네임 or 동아리명", example = "김개똥")
        @NotEmpty
        String nickname,

        @Schema(description = "전화번호", example = "010-1111-1111")
        @Pattern(
                regexp = "^010[\\s.-]?\\d{3,4}[\\s.-]?\\d{4}$",
                message = "휴대폰 번호 형식이 올바르지 않습니다. 예) 010-1234-5678"
        )
        @NotNull
        String phoneNumber,

        @Schema(description = "카테고리 목록", example = "[{\"firstCategory\": 1, \"secondCategory\": 1, \"thirdCategory\": 1}]")
        List<CategoryDto> categoryDtos,

        @NotNull
        @Schema(description = "만 18세 이상입니다.", example = "true여야 합니다.")
        Boolean isSuitableAged,

        @NotNull
        @Schema(description = "개인정보 동의서 확인 여부", example = "true여야 합니다.")
        Boolean isPersonalInfoAgreed,

        @NotNull
        @Schema(description = "서비스 이용 동의서 확인 여부", example = "true여야 합니다.")
        Boolean isServiceUtilizationAgreed,

        @NotNull
        @Schema(description = "마케팅 동의 확인 여부", example = "true or false")
        Boolean isMarketingAgreed
) {
}
