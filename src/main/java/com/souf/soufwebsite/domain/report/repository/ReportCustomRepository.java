package com.souf.soufwebsite.domain.report.repository;

import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminReportResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReportCustomRepository {

    Page<AdminReportResDto> getReportListInAdmin(PostType postType, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable);
}
