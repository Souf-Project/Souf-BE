package com.souf.soufwebsite.domain.report.repository;

import com.souf.soufwebsite.domain.report.entity.ReportReason;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportReasonRepository extends JpaRepository<ReportReason, Long> {
}
