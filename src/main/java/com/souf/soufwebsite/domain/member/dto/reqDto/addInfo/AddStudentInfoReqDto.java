package com.souf.soufwebsite.domain.member.dto.reqDto.addInfo;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.MajorReqDto;
import com.souf.soufwebsite.domain.member.entity.profile.EducationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.List;

public record AddStudentInfoReqDto(

        @Schema(description = "전화번호", example = "010-1111-1111")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "휴대폰 번호 형식이 올바르지 않습니다. 예) 010-1234-5678"
        )
        @NotBlank
        String phoneNumber,

        @NotBlank(message = "학교명은 반드시 입력해주세요.")
        @Schema(description = "학교명 입력은 필수입니다.", example = "세종대학교")
        String schoolName,              // 학교명

        @NotNull(message = "대학교인지 대학원인지 반드시 입력해주세요.")
        @Schema(description = "학력은 필수입니다.", example = "UNIV(대학교) or GRADUATE(대학원)")
        EducationType educationType,    // UNIV or GRADUATE

        @NotNull(message = "전공은 반드시 입력해주세요.")
        @Schema(description = "전공은 최소 하나가 필요합니다.")
        List<MajorReqDto> majorReqDtos, // { 전공명, 전공유형 }

        @NotEmpty(message = "이벤트 알림을 받기 위한 학교 이메일은 필수입니다.")
        @Email
        @Schema(description = "학교 이메일을 입력해주세요.")
        String schoolEmail,             // ac.kr로 끝나는 이메일

        @NotEmpty(message = "학교 인증를 인증하기 위한 파일을 제시해주세요.")
        @Schema(description = "학교 인증 파일은 필수입니다.")
        String schoolAuthenticatedImageFileName // 학생 인증 자료
) {
    @AssertTrue(message = "학교 이메일은 .ac.kr로 끝나야 합니다.")
    public boolean isSchoolEmailValid() {
        return schoolEmail != null && schoolEmail.toLowerCase().endsWith(".ac.kr");
    }
}
