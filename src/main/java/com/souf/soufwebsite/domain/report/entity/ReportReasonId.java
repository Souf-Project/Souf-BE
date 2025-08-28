package com.souf.soufwebsite.domain.report.entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ReportReasonId implements Serializable {
    private Long reportId;
    private Long reasonId;
}
