package com.souf.soufwebsite.domain.application.controller;

import com.souf.soufwebsite.domain.application.dto.req.ApplicationDecisionReqDto;
import com.souf.soufwebsite.domain.application.dto.res.ApplicantResDto;
import com.souf.soufwebsite.domain.application.service.ApplicationService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.ApprovedOnly;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.validation.Valid;
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

    @ApprovedOnly
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

    @ApprovedOnly
    @PatchMapping("/{applicationId}/decision")
    public SuccessResponse<?> decideApplication(
            @CurrentEmail String email,
            @PathVariable Long applicationId,
            @RequestBody @Valid ApplicationDecisionReqDto req
    ) {
        applicationService.decideApplication(email, applicationId, req);
        return new SuccessResponse<>(APPLICATION_DECISION_SUCCESS.getMessage());
    }
}
