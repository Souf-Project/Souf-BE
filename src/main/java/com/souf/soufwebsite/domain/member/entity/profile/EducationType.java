package com.souf.soufwebsite.domain.member.entity.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EducationType {

    UNIV("대학교"),
    GRADUATE("대학원");

    private final String typeName;
}
