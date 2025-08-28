package com.souf.soufwebsite.domain.member.service.admin;

import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminPostResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminReportResDto;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminService {

    Page<AdminPostResDto> getPosts(PostType postType, String writer, String title, Pageable pageable);

    Page<AdminMemberResDto> getMembers(RoleType memberType, String username, String nickname, Pageable pageable);

    Page<AdminReportResDto> getReports(PostType postType, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable);

    void updateReportStatus(Long reportId, ReportStatus reportStatus);
}
