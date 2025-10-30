package com.souf.soufwebsite.domain.member.service.admin;

import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryType;
import com.souf.soufwebsite.domain.member.dto.reqDto.InquiryAnswerReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminMemberResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminPostResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminReportResDto;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import com.souf.soufwebsite.global.common.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AdminService {

    Page<AdminPostResDto> getPosts(PostType postType, String writer, String title, Pageable pageable);

    Page<AdminMemberResDto> getMembers(RoleType memberType, String username, String nickname, Pageable pageable);

    Page<AdminReportResDto> getReports(PostType postType, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable);

    Page<InquiryResDto> getInquiries(String search, InquiryType inquiryType, InquiryStatus status, Pageable pageable);

    void answerInquiry(String email, Long inquiryId, InquiryAnswerReqDto inquiryAnswerReqDto);

    void updateReportStatus(Long reportId, ReportStatus reportStatus);
}
