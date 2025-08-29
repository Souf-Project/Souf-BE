package com.souf.soufwebsite.domain.report.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportReasonMapping {

    @EmbeddedId
    private ReportReasonId id = new ReportReasonId();

    @MapsId("reportId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @MapsId("reasonId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reason_id")
    private Reason reason;

    public ReportReasonMapping(Report report, Reason reason) {
        this.report = report;
        this.reason = reason;
    }
}
