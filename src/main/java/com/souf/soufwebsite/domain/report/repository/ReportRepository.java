package com.souf.soufwebsite.domain.report.repository;

import com.souf.soufwebsite.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportCustomRepository {
}
