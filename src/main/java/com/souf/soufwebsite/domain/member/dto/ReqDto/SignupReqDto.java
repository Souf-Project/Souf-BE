package com.souf.soufwebsite.domain.member.dto.ReqDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record SignupReqDto(

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
        @NotEmpty String passwordCheck
) {
}