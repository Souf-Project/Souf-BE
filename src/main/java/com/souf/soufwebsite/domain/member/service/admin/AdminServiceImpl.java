package com.souf.soufwebsite.domain.member.service.admin;

import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminPostResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminReportResDto;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;
    private final RecruitRepository recruitRepository;
    private final ReportRepository reportRepository;

    @Override
    public Page<AdminPostResDto> getPosts(PostType postType, String writer, String title, Pageable pageable) {
        return null;
    }

    @Override
    public Page<AdminMemberResDto> getMembers(RoleType memberType, String username, String nickname, Pageable pageable) {
        log.info("memberType: {}, username: {}, nickname: {}", memberType, username, nickname);
        return memberRepository.getMemberListInAdmin(memberType, username, nickname, pageable);
    }

    @Override
    public Page<AdminReportResDto> getReports(PostType postType, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable) {
        log.info("postType: {}, startDate: {}, endDate: {}, nickname: {}", postType, startDate, endDate, nickname);
        return reportRepository.getReportListInAdmin(postType, startDate, endDate, nickname, pageable);
    }
}
