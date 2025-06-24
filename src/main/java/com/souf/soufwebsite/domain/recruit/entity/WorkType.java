package com.souf.soufwebsite.domain.recruit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkType {

    ONLINE("온라인"),
    OFFLINE("오프라인");

    private final String workTypeName;
}
