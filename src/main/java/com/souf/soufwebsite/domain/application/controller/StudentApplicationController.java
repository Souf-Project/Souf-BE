package com.souf.soufwebsite.domain.application.controller;


import com.souf.soufwebsite.domain.application.dto.MyApplicationResDto;
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
public class StudentApplicationController implements StudentApplicationApiSpecification{

    private final ApplicationService applicationService;

    @PostMapping("/{recruitId}/apply")
    public SuccessResponse<?> apply(@PathVariable Long recruitId) {
        applicationService.apply(recruitId);
        return new SuccessResponse<>("지원이 완료되었습니다.");
    }

    @DeleteMapping("/{recruitId}/apply")
    public SuccessResponse<?> deleteApplication(@PathVariable Long recruitId) {
        applicationService.deleteApplication(recruitId);
        return new SuccessResponse<>("지원이 취소되었습니다.");
    }

    @GetMapping("/my")
    public SuccessResponse<Page<MyApplicationResDto>> getMyApplications(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return new SuccessResponse<>(applicationService.getMyApplications(pageable));
    }
}
