package com.souf.soufwebsite.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reason;
}
/*
PERSONAL_INFO_EXPOSURE(1L, "개인정보 노출"),
    VIOLENT(2L, "폭력 또는 악의적인 콘텐츠"),
    SENSATIONALISM(3L, "폭력성/선정성"),
    NOT_PROPER_CONTENT(4L, "부적절한 컨텐츠"),
    ABUSE(5L, "욕설/인신공격"),
    COPYRIGHT_INFRINGEMENT(6L, "저작권 침해"),
    REPEATED_CONTENT(7L, "반복성 게시글(도배)"),
    ETC(8L, "기타");*/
