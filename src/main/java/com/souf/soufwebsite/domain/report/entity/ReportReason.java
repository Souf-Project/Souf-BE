package com.souf.soufwebsite.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReportReason {

    PERSONAL_INFO_EXPOSURE(1L, "개인정보 노출"),
    VIOLENT(2L, "폭력 또는 악의적인 콘텐츠"),
    SENSATIONALISM(3L, "폭력성/선정성"),
    NOT_PROPER_CONTENT(4L, "부적절한 컨텐츠"),
    ABUSE(5L, "욕설/인신공격"),
    COPYRIGHT_INFRINGEMENT(6L, "저작권 침해"),
    REPEATED_CONTENT(7L, "반복성 게시글(도배)"),
    ETC(8L, "기타");

    private final Long reasonId;
    private final String reason;

    public static ReportReason of(Long reasonId) {
        return Arrays.stream(ReportReason.values())
                .filter(v -> v.getReasonId().equals(reasonId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid code: " + reasonId));
    }

}
