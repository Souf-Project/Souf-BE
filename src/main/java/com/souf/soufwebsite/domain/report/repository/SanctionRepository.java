package com.souf.soufwebsite.domain.report.repository;

import com.souf.soufwebsite.domain.report.entity.Sanction;
import com.souf.soufwebsite.domain.report.entity.SanctionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SanctionRepository extends JpaRepository<Sanction, Long> {

    boolean existsBySanctionTypeAndReportId(SanctionType type, Long reportId); // STRIKE 중복 방지
    @Query("select count(s) from Sanction s where s.memberId = :memberId and s.sanctionType = 'STRIKE'")
    int countStrikes(@Param("memberId") Long memberId);
}
