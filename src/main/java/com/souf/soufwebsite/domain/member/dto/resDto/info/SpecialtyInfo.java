package com.souf.soufwebsite.domain.member.dto.resDto.info;

import com.souf.soufwebsite.domain.member.entity.profile.MajorType;

public record SpecialtyInfo(
        String specialtyName,
        MajorType specialtyType
) {}