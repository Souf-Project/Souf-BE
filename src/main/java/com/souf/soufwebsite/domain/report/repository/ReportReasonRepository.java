package com.souf.soufwebsite.domain.report.repository;

import com.souf.soufwebsite.domain.report.entity.Reason;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportReasonRepository extends JpaRepository<Reason, Long> {

    List<Reason> findByIdIn(List<Long> ids);
}
