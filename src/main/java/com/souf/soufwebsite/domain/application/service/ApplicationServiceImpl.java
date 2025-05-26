package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.ApplicantResDto;
import com.souf.soufwebsite.domain.application.dto.MyApplicationResDto;
import com.souf.soufwebsite.domain.application.entity.Application;
import com.souf.soufwebsite.domain.application.exception.AlreadyAppliedException;
import com.souf.soufwebsite.domain.application.exception.NotFoundApplicationException;
import com.souf.soufwebsite.domain.application.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.application.exception.NotRecruitableException;
import com.souf.soufwebsite.domain.application.repository.ApplicationRepository;
import com.souf.soufwebsite.domain.member.dto.ResDto.MemberResDto;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.common.category.dto.CategoryDto;
import com.souf.soufwebsite.global.email.EmailService;
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
    private final EmailService emailService;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    @Transactional
    public void apply(Long recruitId) {
        Member member = getCurrentUser();
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(NotFoundRecruitException::new);

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
                    String company = recruit.getMember().getNickname();
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
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(NotFoundRecruitException::new);
        Page<Application> apps = applicationRepository.findByRecruit(recruit, pageable);

        return applicationRepository
                .findByRecruit(recruit, pageable)
                .map(app -> new ApplicantResDto(
                        app.getId(),
                        MemberResDto.from(app.getMember()),
                        app.getAppliedAt(),
                        app.getStatus().name()        // PENDING / ACCEPTED / REJECTED
                ));
    }

    @Override
    @Transactional
    public void reviewApplication(Long recruitId, Long applicationId, boolean approve) {
        Application app = applicationRepository
                .findByIdAndRecruit_Id(applicationId, recruitId)
                .orElseThrow(NotFoundApplicationException::new);

        if (approve) app.accept();
        else        app.reject();

        String to = app.getMember().getEmail();
        String title = app.getRecruit().getTitle();
        String subject = "[Souf] “" + title + "” 지원 " + (approve ? "수락" : "거절") + " 안내";
        String body = new StringBuilder()
                .append(app.getMember().getNickname()).append("님,\n\n")
                .append("“").append(title).append("” 공고에 대한 귀하의 지원이 ")
                .append(approve ? "수락" : "거절").append("되었습니다.\n")
                .append("자세한 사항은 홈페이지에서 확인해 주세요.\n\n")
                .append("감사합니다.")
                .toString();

        emailService.sendEmail(to, subject, body);
    }
}
