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
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/{recruitId}/apply")
    public SuccessResponse<?> apply(
            @PathVariable Long recruitId) {
        applicationService.apply(recruitId);
        return new SuccessResponse<>("지원이 완료되었습니다.");
    }

    @DeleteMapping("/{recruitId}/apply")
    public SuccessResponse<?> deleteApplication(
            @PathVariable Long recruitId
    ) {
        applicationService.deleteApplication(recruitId);
        return new SuccessResponse<>("지원이 취소되었습니다.");
    }

    @GetMapping("/my")
    public SuccessResponse<Page<MyApplicationResDto>> getMyApplications(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getMyApplications(pageable));
    }

    @GetMapping("/{recruitId}/applicants")
    public SuccessResponse<Page<ApplicantResDto>> getApplicantsForRecruit(
            @PathVariable Long recruitId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getApplicantsByRecruit(recruitId, pageable));
    }

    @PostMapping("/{applicationId}/approve")
    public SuccessResponse<?> reviewApplication(
            @PathVariable Long applicationId
    ) {
        applicationService.reviewApplication(applicationId, true);
        return new SuccessResponse<>("지원이 수락되었습니다.");
    }

    @PostMapping("/{applicationId}/reject")
    public SuccessResponse<?> rejectApplication(
            @PathVariable Long applicationId
    ) {
        applicationService.reviewApplication(applicationId, false);
        return new SuccessResponse<>("지원이 거절되었습니다.");
    }
}