package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.req.ApplicationOfferReqDto;
import com.souf.soufwebsite.domain.application.dto.res.ApplicantResDto;
import com.souf.soufwebsite.domain.application.dto.res.MyApplicationResDto;
import com.souf.soufwebsite.domain.application.entity.Application;
import com.souf.soufwebsite.domain.application.exception.*;
import com.souf.soufwebsite.domain.application.repository.ApplicationRepository;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.resDto.MemberResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

        // ✅ [추가] 지원자 생성 → 공고 작성자에게 즉시 알림
        Member owner = recruit.getMember();
        NotificationDto dto = new NotificationDto(
                owner.getEmail(),
                owner.getId(),                                // targetMemberId
                NotificationType.APPLICANT_CREATED,           // type
                "새 지원자 발생",                               // title
                "[" + recruit.getTitle() + "]에 새 지원자가 도착했어요.", // body
                "RECRUIT",                                    // refType
                recruit.getId(),                              // refId
                LocalDateTime.now()                          // createdAt
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
                                "삭제된 공고입니다",
                                null,
                                List.of(),
                                "삭제됨",
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

                    String status = recruit.isRecruitable() ? "모집 중" : "마감";
                    return new MyApplicationResDto(
                            recruit.getId(),
                            recruit.getTitle(),
                            recruit.getMember().getNickname(),
                            categories,
                            status,
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

        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, me.getId());

        return applicationRepository
                .findByRecruit(recruit, pageable)
                .map(app -> new ApplicantResDto(
                        app.getId(),
                        MemberResDto.from(app.getMember(), app.getMember().getCategories(), mediaUrl, false),
                        app.getPriceOffer(),
                        app.getPriceReason(),
                        app.getAppliedAt(),
                        app.getStatus().name()        // PENDING / ACCEPTED / REJECTED
                ));
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
                NotificationType.APPLICATION_REVIEWED,   // 알림 타입
                "지원 결과 안내",                           // 알림 제목
                bodyMsg,                                 // 본문 내용
                "APPLICATION",                           // 참조 타입
                app.getId(),                             // 참조 PK
                LocalDateTime.now()
        );

        notificationPublisher.publish(dto);
        emailService.announceRecruitResult(to, m.getNickname(), title);
    }

    private Member findIfEmailExists(String email) {
        return memberRepository.findByEmail(email).orElseThrow(NotFoundMemberException::new);
    }
}
