package com.souf.soufwebsite.domain.application.controller;

import com.souf.soufwebsite.domain.application.dto.ApplicantResDto;
import com.souf.soufwebsite.domain.application.dto.MyApplicationResDto;
import com.souf.soufwebsite.domain.application.service.ApplicationService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

// ApplicationController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/recruits/{recruitId}/apply")
    public SuccessResponse<?> apply(
            @PathVariable Long recruitId) {
        applicationService.apply(recruitId);
        return new SuccessResponse<>("지원이 완료되었습니다.");
    }

    @DeleteMapping("/recruits/{recruitId}/apply")
    public SuccessResponse<?> deleteApplication(
            @PathVariable Long recruitId
    ) {
        applicationService.deleteApplication(recruitId);
        return new SuccessResponse<>("지원이 취소되었습니다.");
    }

    @GetMapping("/applications/my")
    public SuccessResponse<Page<MyApplicationResDto>> getMyApplications(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getMyApplications(pageable));
    }

    @GetMapping("/recruits/{recruitId}/applications")
    public SuccessResponse<Page<ApplicantResDto>> getApplicationsForRecruit(
            @PathVariable Long recruitId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getApplicantsByRecruit(recruitId, pageable));
    }

    @PostMapping("/recruits/{recruitId}/applications/{applicationId}/approve")
    public SuccessResponse<?> reviewApplication(
            @PathVariable Long recruitId,
            @PathVariable Long applicationId
    ) {
        applicationService.reviewApplication(recruitId, applicationId, true);
        return new SuccessResponse<>("지원이 수락되었습니다.");
    }

    @PostMapping("/recruits/{recruitId}/applications/{applicationId}/reject")
    public SuccessResponse<?> rejectApplication(
            @PathVariable Long recruitId,
            @PathVariable Long applicationId
    ) {
        applicationService.reviewApplication(recruitId, applicationId, false);
        return new SuccessResponse<>("지원이 거절되었습니다.");
    }
}