package com.souf.soufwebsite.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApprovedStatus {

    PENDING("승인 대기 중"),
    APPROVED("승인 완료"),
    REJECTED("승인 거절");

    private final String status;
}
