package com.souf.soufwebsite.domain.application.controller;

import com.souf.soufwebsite.domain.application.dto.res.ApplicantResDto;
import com.souf.soufwebsite.domain.application.service.ApplicationService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static com.souf.soufwebsite.domain.application.controller.ApplicationSuccessMessage.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/applications")
public class RecruitApplicationController implements RecruitApplicationApiSpecification {

    private final ApplicationService applicationService;

    @GetMapping("/{recruitId}/applicants")
    public SuccessResponse<Page<ApplicantResDto>> getApplicantsForRecruit(
            @CurrentEmail String email,
            @PathVariable Long recruitId,
            @PageableDefault Pageable pageable
    ) {
        return new SuccessResponse<>(
                applicationService.getApplicantsByRecruit(email, recruitId, pageable),
                APPLICATION_READ_SUCCESS.getMessage());
    }

    @PostMapping("/{applicationId}/approve")
    public SuccessResponse<?> approveApplication(
            @CurrentEmail String email,
            @PathVariable Long applicationId) {
        applicationService.reviewApplication(email, applicationId, true);
        return new SuccessResponse<>(APPLY_ACCEPT.getMessage());
    }

    @PostMapping("/{applicationId}/reject")
    public SuccessResponse<?> rejectApplication(
            @CurrentEmail String email,
            @PathVariable Long applicationId) {
        applicationService.reviewApplication(email, applicationId, false);
        return new SuccessResponse<>(APPLY_REJECT.getMessage());
    }
}
