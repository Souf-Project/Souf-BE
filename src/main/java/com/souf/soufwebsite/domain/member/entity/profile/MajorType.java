package com.souf.soufwebsite.domain.member.entity.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MajorType {

    MAJOR("주전공"),
    MINOR("부전공"),
    DOUBLE_MAJOR("복수전공");

    private final String type;
}
