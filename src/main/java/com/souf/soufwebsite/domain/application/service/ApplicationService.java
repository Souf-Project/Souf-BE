package com.souf.soufwebsite.domain.application.service;

import com.souf.soufwebsite.domain.application.dto.ApplicationReqDto;
import com.souf.soufwebsite.domain.application.dto.ApplicationResDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {
    void apply(ApplicationReqDto reqDto);
    Page<ApplicationResDto> getMyApplications(Pageable pageable);
    Page<ApplicationResDto> getApplicationForRecruit(Long recruitId, Pageable pageable);

    void deleteApplication(ApplicationReqDto reqDto);
}
