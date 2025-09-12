package com.souf.soufwebsite.domain.report.service;

import com.souf.soufwebsite.domain.feed.exception.NotFoundFeedException;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.report.dto.ReportReqDto;
import com.souf.soufwebsite.domain.report.entity.Reason;
import com.souf.soufwebsite.domain.report.entity.Report;
import com.souf.soufwebsite.domain.report.entity.ReportReasonMapping;
import com.souf.soufwebsite.domain.report.exception.NotMatchedReportOwnerException;
import com.souf.soufwebsite.domain.report.repository.ReasonRepository;
import com.souf.soufwebsite.domain.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final ReasonRepository reasonRepository;

    @Transactional
    @Override
    public void createReport(String email, ReportReqDto reqDto) {
        Member currentMember = findIfEmailExists(email);

        Member reporter = findIfMemberExists(reqDto.reporterId());
        Member reportedMember = findIfMemberExists(reqDto.reportedMemberId());

        if(!currentMember.getId().equals(reporter.getId())) {
            throw new NotMatchedReportOwnerException();
        }

        Report report = new Report(reqDto.description(), reporter, reportedMember,
                reqDto.postType(), reqDto.postId(), reqDto.title());

        List<Reason> reasons = reasonRepository.findByIdIn(reqDto.reasons());
        for(Reason reason : reasons) {
            log.info("reason: {}", reason.getId());
            ReportReasonMapping reportReasonMapping = new ReportReasonMapping(report, reason);
            report.addReportReasonMapping(reportReasonMapping);
        }
        reportRepository.save(report);
        log.info("신고글이 생성되었습니다!: {}", report);
    }

    private Member findIfMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundFeedException::new);
    }

    private Member findIfEmailExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundFeedException::new);
    }
}
