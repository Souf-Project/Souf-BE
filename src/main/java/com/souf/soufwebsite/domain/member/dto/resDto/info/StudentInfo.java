package com.souf.soufwebsite.domain.member.dto.resDto.info;


import com.souf.soufwebsite.domain.member.entity.profile.EducationType;

import java.util.List;


public record StudentInfo(
        String schoolName,
        EducationType educationType,
        String schoolEmail,
        List<SpecialtyInfo> specialties
) implements MemberInfo {}