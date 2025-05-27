package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.ApplicantResDto;
import com.souf.soufwebsite.domain.application.dto.MyApplicationResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
    void apply(Long recruitId);
    void deleteApplication(Long recruitId);
    Page<MyApplicationResDto> getMyApplications(Pageable pageable);
    Page<ApplicantResDto> getApplicantsByRecruit(Long recruitId, Pageable pageable);
    void reviewApplication(Long applicationId, boolean approve);
}
