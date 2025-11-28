package com.souf.soufwebsite.domain.recruit.contract.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record BeneficiaryReqDto(

        @Schema(description = "수급자 본명을 입력해주세요.", example = "김철수")
        @NotBlank(message = "수급자 본명은 필수입니다.")
        String username,

        @Schema(description = "생년월일을 입력해주세요.", example = "2000-11-11")
        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate birth,

        @Schema(description = "학교명 및 소속 학과를 입력해주세요.", example = "세종대학교 컴퓨터공학과(졸)")
        @NotBlank(message = "학교명 및 소속 학과는 필수입니다.")
        String schoolName,

        @Schema(description = "작업 중 연락을 취할 이메일을 입력해주세요.", example = "abcd1234@gmail.com")
        @Email(message = "이메일 형식에 맞게 입력해주세요.")
        @NotBlank(message = "이메일은 필수입니다.")
        String email,

        @Schema(description = "수급자의 전화번호를 입력해주세요.", example = "010-xxxx-xxxx")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "휴대폰 번호 형식이 올바르지 않습니다. 예) 010-1234-5678"
        )
        @NotBlank(message = "수급자 전화번호는 필수입니다.")
        String phoneNumber,

        @Schema(description = "지급받을 은행을 입력해주세요.", example = "카카오뱅크")
        @NotBlank(message = "은행명은 필수입니다.")
        String bank,

        @Schema(description = "지급받을 은행 계좌를 입력해주세요.", example = "1234-12-1234567")
        @NotBlank(message = "은행 계좌 입력은 필수입니다.")
        String bankAccount
) {
}
