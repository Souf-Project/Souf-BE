package com.souf.soufwebsite.domain.application.controller;

import com.souf.soufwebsite.domain.application.dto.ApplicationReqDto;
import com.souf.soufwebsite.domain.application.dto.ApplicationResDto;
import com.souf.soufwebsite.domain.application.service.ApplicationService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

// ApplicationController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/recruits/{recruitId}/apply")
    public SuccessResponse<?> apply(ApplicationReqDto reqDto) {
        applicationService.apply(reqDto);
        return new SuccessResponse<>("지원이 완료되었습니다.");
    }

    @GetMapping("/members/me/applications")
    public SuccessResponse<Page<ApplicationResDto>> getMyApplications(
            Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getMyApplications(pageable));
    }

    @GetMapping("/recruits/{recruitId}/applications")
    public SuccessResponse<Page<ApplicationResDto>> getApplicationsForRecruit(
            @PathVariable Long recruitId,
            Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getApplicationForRecruit(recruitId, pageable));
    }

    @DeleteMapping("/recruits/{recruitId}/apply")
    public SuccessResponse<?> deleteApplication(
            @PathVariable Long recruitId
    ) {
        ApplicationReqDto reqDto = new ApplicationReqDto(recruitId);
        applicationService.deleteApplication(reqDto);
        return new SuccessResponse<>("지원이 취소되었습니다.");
    }
}