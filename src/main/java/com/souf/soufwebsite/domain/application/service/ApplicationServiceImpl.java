package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.ApplicantResDto;
import com.souf.soufwebsite.domain.application.dto.MyApplicationResDto;
import com.souf.soufwebsite.domain.application.entity.Application;
import com.souf.soufwebsite.domain.application.exception.*;
import com.souf.soufwebsite.domain.application.repository.ApplicationRepository;
import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.common.mail.SesMailService;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final RecruitRepository recruitRepository;
    private final SesMailService emailService;
    private final FileService fileService;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    private void verifyOwner(Recruit recruit, Member member) {
        if (!recruit.getMember().getId().equals(member.getId())) {
            throw new NotValidAuthenticationException();
        }
    }

    @Override
    @Transactional
    public void apply(Long recruitId) {
        Member member = getCurrentUser();
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

        Application application = Application.of(member, recruit);
        recruit.increaseRecruitCount();
        applicationRepository.save(application);
    }

    @Override
    @Transactional
    public void deleteApplication(Long recruitId) {
        Member member = getCurrentUser();
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(NotFoundRecruitException::new);

        Application application = applicationRepository.findByMemberAndRecruit(member, recruit)
                .orElseThrow(NotFoundApplicationException::new);

        applicationRepository.delete(application);
        recruit.decreaseRecruitCount();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MyApplicationResDto> getMyApplications(Pageable pageable) {
        Member me = getCurrentUser();
        return applicationRepository.findByMember(me, pageable)
                .map(app -> {
                    Recruit recruit = app.getRecruit();

                    List<CategoryDto> categories = recruit.getCategories().stream()
                            .map(mapping -> new CategoryDto(
                                    mapping.getFirstCategory().getId(),
                                    mapping.getSecondCategory().getId(),
                                    mapping.getThirdCategory().getId()
                            ))
                            .toList();

                    String status = recruit.isRecruitable() ? "모집 중" : "마감";
                    return new MyApplicationResDto(
                            recruit.getId(),
                            recruit.getTitle(),
                            recruit.getMember().getNickname(),
                            categories,
                            status,
                            app.getAppliedAt()
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicantResDto> getApplicantsByRecruit(Long recruitId, Pageable pageable) {
        Member me = getCurrentUser();
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(NotFoundRecruitException::new);
        verifyOwner(recruit, me);

        String mediaUrl = fileService.getMediaUrl(PostType.PROFILE, me.getId());

        return applicationRepository
                .findByRecruit(recruit, pageable)
                .map(app -> new ApplicantResDto(
                        app.getId(),
                        MemberResDto.from(app.getMember(), app.getMember().getCategories(), mediaUrl, false),
                        app.getAppliedAt(),
                        app.getStatus().name()        // PENDING / ACCEPTED / REJECTED
                ));
    }

    @Override
    @Transactional
    public void reviewApplication(Long applicationId, boolean approve) {
        Member me = getCurrentUser();

        Application app = applicationRepository.findById(applicationId)
                .orElseThrow(NotFoundApplicationException::new);
        Recruit recruit = app.getRecruit();
        verifyOwner(recruit, me);

        if (approve) app.accept();
        else        app.reject();

        Member m = app.getMember();
        String to = m.getEmail();
        String title = app.getRecruit().getTitle();


        emailService.announceRecruitResult(to, m.getNickname(), title);
    }
}
