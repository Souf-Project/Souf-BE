package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.req.ApplicationOfferReqDto;
import com.souf.soufwebsite.domain.application.dto.res.ApplicantResDto;
import com.souf.soufwebsite.domain.application.dto.res.MyApplicationResDto;
import com.souf.soufwebsite.domain.application.entity.Application;
import com.souf.soufwebsite.domain.application.entity.ApplicationStatus;
import com.souf.soufwebsite.domain.application.exception.*;
import com.souf.soufwebsite.domain.application.repository.ApplicationRepository;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.entity.MemberCategoryMapping;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.domain.notification.dto.NotificationDto;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import com.souf.soufwebsite.domain.notification.service.NotificationPublisher;
import com.souf.soufwebsite.domain.recruit.entity.PricePolicy;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.mail.SesMailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RecruitRepository recruitRepository;
    private final SesMailService emailService;
    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final NotificationPublisher notificationPublisher;

    private void verifyOwner(Recruit recruit, Member member) {
        if (!recruit.getMember().getId().equals(member.getId())) {
            throw new NotValidAuthenticationException();
        }
    }

    @Override
    @Transactional
    public void apply(String email, Long recruitId, ApplicationOfferReqDto reqDto) {
        Member member = findIfEmailExists(email);
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(NotFoundRecruitException::new);

        if (recruit.getMember().getId().equals(member.getId())) {
            throw new NotApplyMyRecruitException();
        }
        log.info("본인 공고에 본인이 지원 못하는 유효성 검사 통과");

        if (!recruit.isRecruitable()) {
            throw new NotRecruitableException();
        }

        if (applicationRepository.existsByMemberAndRecruit(member, recruit)) {
            throw new AlreadyAppliedException();
        }

        Application application;
        if (recruit.getPricePolicy() == PricePolicy.FIXED) {
            application = Application.applyFixed(member, recruit);
        } else { // OFFER
            if (reqDto == null) {
                throw new OfferRequiredException();
            }
            application = Application.applyOffer(member, recruit, reqDto.priceOffer(), reqDto.priceReason());
        }

        recruit.increaseRecruitCount();
        applicationRepository.save(application);

        Member recruiter = recruit.getMember();
        Long totalCount = applicationRepository.countByRecruit(recruit);
        emailService.sendApplyProgress(recruiter.getEmail(), recruiter.getNickname(), recruit.getTitle(), totalCount);
        log.info("공고문 아이디: {}, 지원 완료", recruit.getId());

        Member owner = recruit.getMember();
        NotificationDto dto = new NotificationDto(
                owner.getEmail(),
                owner.getId(),
                NotificationType.APPLICANT_CREATED,
                "새 지원자 발생",
                "[" + recruit.getTitle() + "]에 새 지원자가 도착했어요.",
                "RECRUIT",
                recruit.getId(),
                LocalDateTime.now()
        );
        notificationPublisher.publish(dto);
    }

    @Override
    @Transactional
    public void deleteApplicationById(String email, Long applicationId) {
        Member me = findIfEmailExists(email);

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(NotFoundApplicationException::new);

        if (!app.getMember().getId().equals(me.getId())) {
            throw new NotValidAuthenticationException();
        }

        Recruit recruit = app.getRecruit();
        applicationRepository.delete(app);
        if (recruit != null) {
            recruit.decreaseRecruitCount();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MyApplicationResDto> getMyApplications(String email, Pageable pageable) {
        Member me = findIfEmailExists(email);
        return applicationRepository.findByMember(me, pageable)
                .map(app -> {
                    Recruit recruit = app.getRecruit();

                    if (recruit == null) {
                        return new MyApplicationResDto(
                                null,
                                null,
                                "삭제된 공고입니다",
                                null,
                                List.of(),
                                "삭제됨",
                                ApplicationStatus.PENDING,
                                app.getPriceOffer(),
                                app.getPriceReason(),
                                app.getAppliedAt()
                        );
                    }

                    List<CategoryDto> categories = recruit.getCategories().stream()
                            .map(m -> new CategoryDto(
                                    m.getFirstCategory().getId(),
                                    m.getSecondCategory() != null ? m.getSecondCategory().getId() : null,
                                    m.getThirdCategory()  != null ? m.getThirdCategory().getId()  : null
                            ))
                            .toList();

                    String recruitable = recruit.isRecruitable() ? "모집 중" : "마감";
                    String writerNickname = "탈퇴한 회원";
                    try {
                        if (recruit.getMember() != null) {
                            writerNickname = recruit.getMember().getNickname();
                        }
                    } catch (EntityNotFoundException ignored) {
                    }

                    return new MyApplicationResDto(
                            recruit.getId(),
                            app.getId(),
                            recruit.getTitle(),
                            writerNickname,
                            categories,
                            recruitable,
                            app.getStatus(),
                            app.getPriceOffer(),
                            app.getPriceReason(),
                            app.getAppliedAt()
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicantResDto> getApplicantsByRecruit(String email, Long recruitId, Pageable pageable) {
        Member me = findIfEmailExists(email);
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(NotFoundRecruitException::new);
        verifyOwner(recruit, me);

        return applicationRepository
                .findByRecruit(recruit, pageable)
                .map(app -> {
                    Member applicant = app.getMember();
                    String applicantProfileImage = fileService.getMediaUrl(PostType.PROFILE, applicant.getId());
                    List<MemberCategoryMapping> categories =
                            (applicant != null) ? applicant.getCategories() : List.of();

                    MemberResDto memberDto = MemberResDto.from(
                            applicant,
                            categories,
                            applicantProfileImage,
                            false
                    );

                    return new ApplicantResDto(
                            app.getId(),
                            memberDto,
                            app.getPriceOffer(),
                            app.getPriceReason(),
                            app.getAppliedAt(),
                            app.getStatus().name()
                    );
                });
    }

    @Override
    @Transactional
    public void reviewApplication(String email, Long applicationId, boolean approve) {
        Member me = findIfEmailExists(email);

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(NotFoundApplicationException::new);

        Recruit recruit = app.getRecruit();
        if (recruit == null) {
            throw new NotFoundRecruitException();
        }

        verifyOwner(recruit, me);

        if (approve) app.accept();
        else        app.reject();

        Member m = app.getMember();
        String to = m.getEmail();
        String title = app.getRecruit().getTitle();


        String bodyMsg = "[" + recruit.getTitle() + "] 지원에 대한 결과가 등록되었습니다.";

        NotificationDto dto = new NotificationDto(
                m.getEmail(),
                m.getId(),
                NotificationType.APPLICATION_REVIEWED,
                "지원 결과 안내",
                bodyMsg,
                "APPLICATION",
                app.getId(),
                LocalDateTime.now()
        );

        notificationPublisher.publish(dto);
        emailService.announceRecruitResult(to, m.getNickname(), title);
    }

    private Member findIfEmailExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }
}
