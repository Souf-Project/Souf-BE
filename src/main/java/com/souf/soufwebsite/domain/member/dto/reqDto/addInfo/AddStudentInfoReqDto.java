package com.souf.soufwebsite.domain.member.dto.reqDto.addInfo;

import com.souf.soufwebsite.domain.member.dto.reqDto.signup.MajorReqDto;
import com.souf.soufwebsite.domain.member.entity.profile.EducationType;

import java.util.List;

public record AddStudentInfoReqDto(
        String phoneNumber,
        String schoolName,              // 학교명
        EducationType educationType,    // UNIV or GRADUATE
        List<MajorReqDto> majorReqDtos, // { 전공명, 전공유형 }
        String schoolEmail,             // ac.kr로 끝나는 이메일
        String schoolAuthenticatedImageFileName // 학생 인증 자료
) {
}
