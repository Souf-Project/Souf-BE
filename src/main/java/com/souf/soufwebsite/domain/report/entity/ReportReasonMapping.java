package com.souf.soufwebsite.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportReasonMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    private ReportReason reasonReason;

    public ReportReasonMapping(Report report, ReportReason reasonReason) {
        this.report = report;
        this.reasonReason = reasonReason;
    }
}
