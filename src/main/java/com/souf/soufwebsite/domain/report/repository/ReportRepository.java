package com.souf.soufwebsite.domain.report.repository;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.report.entity.Report;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportCustomRepository {

    Long countByStatusAndReportedMember(ReportStatus status, Member reportedMember);
}
