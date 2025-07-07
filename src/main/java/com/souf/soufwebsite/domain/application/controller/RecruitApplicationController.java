package com.souf.soufwebsite.domain.application.controller;

import com.souf.soufwebsite.domain.application.dto.ApplicantResDto;
import com.souf.soufwebsite.domain.application.service.ApplicationService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applications")
public class RecruitApplicationController implements RecruitApplicationApiSpecification {

    private final ApplicationService applicationService;

    @GetMapping("/{recruitId}/applicants")
    public SuccessResponse<Page<ApplicantResDto>> getApplicantsForRecruit(
            @PathVariable Long recruitId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getApplicantsByRecruit(recruitId, pageable));
    }

    @PostMapping("/{applicationId}/approve")
    public SuccessResponse<?> reviewApplication(@PathVariable Long applicationId) {
        applicationService.reviewApplication(applicationId, true);
        return new SuccessResponse<>("지원이 수락되었습니다.");
    }

    @PostMapping("/{applicationId}/reject")
    public SuccessResponse<?> rejectApplication(@PathVariable Long applicationId) {
        applicationService.reviewApplication(applicationId, false);
        return new SuccessResponse<>("지원이 거절되었습니다.");
    }
}
