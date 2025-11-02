package com.souf.soufwebsite.domain.application.controller;


import com.souf.soufwebsite.domain.application.dto.req.ApplicationOfferReqDto;
import com.souf.soufwebsite.domain.application.dto.res.MyApplicationResDto;
import com.souf.soufwebsite.domain.application.service.ApplicationService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.ApprovedOnly;
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
public class StudentApplicationController implements StudentApplicationApiSpecification{

    private final ApplicationService applicationService;

    @ApprovedOnly
    @PostMapping("/{recruitId}/apply")
    public SuccessResponse<?> apply(
            @CurrentEmail String email,
            @PathVariable Long recruitId,
            @RequestBody(required = false) ApplicationOfferReqDto reqDto) {
        applicationService.apply(email, recruitId, reqDto);
        return new SuccessResponse<>(APPLY_SUCCESS.getMessage());
    }

    @ApprovedOnly
    @DeleteMapping("/{applicationId}")
    public SuccessResponse<?> deleteApplication(
            @CurrentEmail String email,
            @PathVariable Long applicationId
    ) {
        applicationService.deleteApplicationById(email, applicationId);
        return new SuccessResponse<>(APPLY_CANCELED.getMessage());
    }

    @GetMapping("/my")
    public SuccessResponse<Page<MyApplicationResDto>> getMyApplications(
            @CurrentEmail String email,
            @PageableDefault Pageable pageable
    ) {
        return new SuccessResponse<>(
                applicationService.getMyApplications(email, pageable),
                MY_APPLICATION_READ_SUCCESS.getMessage());
    }
}
