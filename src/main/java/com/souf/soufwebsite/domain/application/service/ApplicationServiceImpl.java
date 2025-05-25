package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.ApplicationReqDto;
import com.souf.soufwebsite.domain.application.dto.ApplicationResDto;
import com.souf.soufwebsite.domain.application.entity.Application;
import com.souf.soufwebsite.domain.application.exception.AlreadyAppliedException;
import com.souf.soufwebsite.domain.application.exception.NotFoundRecruitException;
import com.souf.soufwebsite.domain.application.repository.ApplicationRepository;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.recruit.entity.Recruit;
import com.souf.soufwebsite.domain.recruit.repository.RecruitRepository;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SecurityUtils securityUtils;
    private final RecruitRepository recruitRepository;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @Override
    @Transactional
    public void apply(ApplicationReqDto reqDto) {
        Member member = getCurrentUser();
        Recruit recruit = recruitRepository.findById(reqDto.recruitId())
                .orElseThrow(NotFoundRecruitException::new);

        if (applicationRepository.existsByMemberAndRecruit(member, recruit)) {
            throw new AlreadyAppliedException();
        }

        Application application = Application.of(member, recruit);
        recruit.increaseRecruitCount();
        applicationRepository.save(application);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResDto> getApplications(Pageable pageable) {
        Member member = getCurrentUser();
        return applicationRepository.findByMember(member, pageable)
                .map(app -> new ApplicationResDto(
                        app.getId(),
                        app.getRecruit().getId(),
                        member.getId(),
                        app.getAppliedAt().toString()
                ));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ApplicationResDto> getApplicationForRecruit(Long recruitId, Pageable pageable) {
        Recruit recruit = recruitRepository.findById(recruitId)
                .orElseThrow(NotFoundRecruitException::new);
        return applicationRepository.findByRecruit(recruit, pageable)
                .map(app -> new ApplicationResDto(
                        app.getId(),
                        recruitId,
                        app.getMember().getId(),
                        app.getAppliedAt().toString()
                ));
    }
}
