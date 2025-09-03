package com.souf.soufwebsite.domain.report.controller;

import com.souf.soufwebsite.domain.report.dto.ReportReqDto;
import com.souf.soufwebsite.domain.report.service.ReportService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.souf.soufwebsite.domain.report.controller.ReportSuccessMessage.REPORT_CREATE;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/report")
public class ReportController implements ReportApiSpecification {

    private final ReportService reportService;

    @PostMapping
    public SuccessResponse createReport(
            @CurrentEmail String email,
            @RequestBody @Valid ReportReqDto reportReqDto
    ) {
        reportService.createReport(email, reportReqDto);

        return new SuccessResponse(REPORT_CREATE.getMessage());
    }
}
