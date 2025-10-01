package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.req.ApplicationOfferReqDto;
import com.souf.soufwebsite.domain.application.dto.res.ApplicantResDto;
import com.souf.soufwebsite.domain.application.dto.res.MyApplicationResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
    void apply(String email, Long recruitId, ApplicationOfferReqDto reqDto);
    void deleteApplication(String email, Long recruitId);
    Page<MyApplicationResDto> getMyApplications(String email, Pageable pageable);
    Page<ApplicantResDto> getApplicantsByRecruit(String email, Long recruitId, Pageable pageable);
    void reviewApplication(String email, Long applicationId, boolean approve);
}
