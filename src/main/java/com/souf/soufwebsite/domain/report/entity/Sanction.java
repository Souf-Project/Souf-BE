package com.souf.soufwebsite.domain.report.entity;

import com.souf.soufwebsite.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sanction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId; // 신고받은 인원

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SanctionType sanctionType;

    @Column
    private Instant untilAt; // 1, 2회 신고 적발 시 사용할 컬럼
    @Column
    private Long reportId;
    @Column
    private String reason; // 어떤 게시글로 제재되었는지 표기

    private Sanction(Long memberId, SanctionType type,
                     Instant untilAt, Long reportId, String reason) {
        this.memberId = memberId;
        this.sanctionType = type;
        this.untilAt = untilAt;
        this.reportId = reportId;
        this.reason = reason;
    }

    public static Sanction strike(Long memberId, Long reportId) {
        return new Sanction(memberId, SanctionType.STRIKE, null,
                reportId, "strike by report:" + reportId);
    }

    public static Sanction tempBan(Long memberId, Duration d, String reason) {

        Instant now = Instant.now();
        return new Sanction(memberId, SanctionType.TEMP_BAN, now.plus(d),
                null, reason);
    }

    public static Sanction permBan(Long memberId, String reason) {
        return new Sanction(memberId, SanctionType.PERM_BAN,
                null, null, reason);
    }
}
