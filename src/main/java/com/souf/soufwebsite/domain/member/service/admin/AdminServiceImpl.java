package com.souf.soufwebsite.domain.member.service.admin;

import com.souf.soufwebsite.domain.feed.entity.Feed;
import com.souf.soufwebsite.domain.feed.repository.FeedRepository;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.exception.NotFoundMediaException;
import com.souf.soufwebsite.domain.file.repository.MediaRepository;
import com.souf.soufwebsite.domain.file.service.MediaCleanupPublisher;
import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.domain.inquiry.entity.Inquiry;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryType;
import com.souf.soufwebsite.domain.inquiry.exception.NotFoundInquiryException;
import com.souf.soufwebsite.domain.inquiry.repository.InquiryRepository;
import com.souf.soufwebsite.domain.member.dto.reqDto.InquiryAnswerReqDto;
import com.souf.soufwebsite.domain.member.dto.reqDto.signup.ResubmitReasonReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminMemberResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminPostResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminReportResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.ProfileResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.info.MemberInfo;
import com.souf.soufwebsite.domain.member.dto.resDto.info.MemberInfoResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.info.MemberResAssembler;
import com.souf.soufwebsite.domain.member.entity.ApprovedStatus;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.notification.service.NotificationPublisher;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.domain.report.entity.Report;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import com.souf.soufwebsite.domain.report.exception.NotFoundReportException;
import com.souf.soufwebsite.domain.report.repository.ReportRepository;
import com.souf.soufwebsite.domain.report.service.StrikeService;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.mail.SesMailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final FeedRepository feedRepository;
    private final RecruitRepository recruitRepository;
    private final ReportRepository reportRepository;
    private final InquiryRepository inquiryRepository;
    private final MediaRepository mediaRepository;

    private final StrikeService strikeService;
    private final SesMailService emailService;
    private final NotificationPublisher notificationPublisher;
    private final MediaCleanupPublisher mediaCleanupPublisher;

    @Override
    @Transactional(readOnly = true)
    public Page<AdminPostResDto> getPosts(PostType postType, String writer, String title, Pageable pageable) {
        log.info("postType: {}, writer: {}, title: {}", postType, writer, title);

        if(postType.equals(PostType.FEED)){
            Page<Feed> feeds = feedRepository.findByMemberAndTopic(writer, title, pageable);
            return feeds.map(
                    AdminPostResDto::fromFeed
            );
        }

        Page<Recruit> recruits = recruitRepository.findByMemberAndTopic(writer, title, pageable);
        return recruits.map(
                AdminPostResDto::fromRecruit
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminMemberResDto> getMembers(RoleType memberType, String username, String nickname, ApprovedStatus approvedStatus, Pageable pageable) {
        log.info("memberType: {}, username: {}, nickname: {}", memberType, username, nickname);
        return memberRepository.getMemberListInAdmin(memberType, username, nickname, approvedStatus, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileResDto getAuthenticationElement(Long memberId) {
        Member user = findIfMemberExists(memberId);
        List<Media> mediaList = mediaRepository.findByPostTypeAndPostId(PostType.AUTHENTICATION, memberId);
        Media authenticationMetadata = mediaList.isEmpty() ? null : mediaList.get(0);
        String url = "";
        if(authenticationMetadata != null) {
            url = authenticationMetadata.getOriginalUrl();
        }

        MemberInfoResDto<? extends MemberInfo> from = MemberResAssembler.from(user, null, "");
        return new ProfileResDto(from, url);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminReportResDto> getReports(PostType postType, LocalDate startDate, LocalDate endDate, String nickname, Pageable pageable) {
        log.info("postType: {}, startDate: {}, endDate: {}, nickname: {}", postType, startDate, endDate, nickname);
        return reportRepository.getReportListInAdmin(postType, startDate, endDate, nickname, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResDto> getInquiries(String search, InquiryType inquiryType, InquiryStatus status, Pageable pageable) {
        log.info("inquiryType: {}, pageable: {}", inquiryType, pageable);

        return inquiryRepository.getInquiryListInAdmin(search, inquiryType, status, pageable);
    }

    @Transactional
    @Override
    public void answerInquiry(String email, Long inquiryId, InquiryAnswerReqDto reqDto) {
        Inquiry inquiry = findIfInquiryExists(inquiryId);
        inquiry.updateAnswer(reqDto);

        Member toMember = inquiry.getMember();

//        NotificationDto dto = new NotificationDto(
//                toMember.getEmail(),
//                toMember.getId(),
//                NotificationType.INQUIRY_REPLIED,
//                "문의에 답변이 등록됐어요",
//                "문의하신 내용에 새로운 답변이 도착했어요.",
//                "INQUIRY",
//                inquiryId,
//                LocalDateTime.now()
//        );

        emailService.sendInquiryResult(toMember.getEmail(), toMember.getNickname(), inquiry.getTitle());
//        notificationPublisher.publish(dto);
    }

    @Transactional
    @Override
    public void updateReportStatus(Long reportId, ReportStatus reportStatus) {
        Report report = findIfReportExists(reportId);
        report.updateStatus(reportStatus);

        if (reportStatus.equals(ReportStatus.RESOLVED) && report.getReportedMember().getId() != null) {
            strikeService.addStrike(report.getReportedMember().getId(), report.getId());
        }
    }

    @Override
    @Transactional
    public void updateApprovedStatus(Long memberId, ApprovedStatus approvedStatus, ResubmitReasonReqDto reqDto) {
        Member member = findIfMemberExists(memberId);
        member.updateApprovedStatus(approvedStatus);

        if(approvedStatus.equals(ApprovedStatus.APPROVED)){
            emailService.sendSignupApprovedResult(member.getEmail(), member.getNickname());
        }
        else if(approvedStatus.equals(ApprovedStatus.REJECTED)){
            emailService.sendSignupRejectedResult(member.getEmail(), member.getNickname(), reqDto.reason(), "");

            Media media = mediaRepository.findFirstByOriginalUrlEndingWithIgnoreCase(reqDto.originalUrl()).orElseThrow(NotFoundMediaException::new);
            mediaCleanupPublisher.publishUrls(PostType.AUTHENTICATION, member.getId(), List.of(media.getOriginalUrl()));

            memberRepository.delete(member);
        }
    }

    private Report findIfReportExists(Long reportId) {
        return reportRepository.findById(reportId).orElseThrow(NotFoundReportException::new);
    }

    private Member findIfMemberExists(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
    }

    private Inquiry findIfInquiryExists(Long inquiryId) {
        return inquiryRepository.findById(inquiryId).orElseThrow(NotFoundInquiryException::new);
    }
}
