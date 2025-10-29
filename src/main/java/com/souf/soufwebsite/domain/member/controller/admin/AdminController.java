package com.souf.soufwebsite.domain.member.controller.admin;

import com.souf.soufwebsite.domain.inquiry.dto.InquiryResDto;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryStatus;
import com.souf.soufwebsite.domain.inquiry.entity.InquiryType;
import com.souf.soufwebsite.domain.member.dto.reqDto.InquiryAnswerReqDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminMemberResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminPostResDto;
import com.souf.soufwebsite.domain.member.dto.resDto.AdminReportResDto;
import com.souf.soufwebsite.domain.member.entity.ApprovedStatus;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.service.admin.AdminService;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.souf.soufwebsite.domain.member.controller.admin.AdminSuccessMessage.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
public class AdminController implements AdminApiSpecification{

    private final AdminService adminService;

    @GetMapping("/post")
    public SuccessResponse<Page<AdminPostResDto>> getPosts(
            @RequestParam(name = "postType") PostType postType,
            @RequestParam(name = "writer", required = false) String writer,
            @RequestParam(name = "title", required = false) String title,
            @PageableDefault Pageable pageable
    ) {

        Page<AdminPostResDto> posts = adminService.getPosts(postType, writer, title, pageable);

        return new SuccessResponse<>(posts, POST_GET_SUCCESS.getMessage());
    }

    @GetMapping("/member")
    public SuccessResponse<Page<AdminMemberResDto>> getMembers(
            @RequestParam(name = "memberType", required = false) RoleType memberType,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "nickname", required = false) String nickname,
            @PageableDefault Pageable pageable
    ) {
        Page<AdminMemberResDto> members = adminService.getMembers(memberType, username, nickname, pageable);

        return new SuccessResponse<>(members, MEMBER_GET_SUCCESS.getMessage());
    }

    @GetMapping("/report")
    public SuccessResponse<Page<AdminReportResDto>> getReports(
            @RequestParam(name = "postType", required = false) PostType postType,
            @RequestParam(name = "startDate", required = false) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) LocalDate endDate,
            @RequestParam(name = "nickname", required = false) String nickname,
            @PageableDefault Pageable pageable
    ) {
        Page<AdminReportResDto> reports = adminService.getReports(postType, startDate, endDate, nickname, pageable);

        return new SuccessResponse<>(reports, REPORT_GET_SUCCESS.getMessage());
    }

    @GetMapping("/inquiry")
    public SuccessResponse<Page<InquiryResDto>> getInquiries(
            @RequestParam(name = "inquiryType", required = false)InquiryType inquiryType,
            @RequestParam(name = "inquiryStatus", required = false) InquiryStatus inquiryStatus,
            @RequestParam(name = "search", required = false) String search,
            @PageableDefault Pageable pageable
    ) {
        Page<InquiryResDto> inquiries = adminService.getInquiries(search, inquiryType, inquiryStatus, pageable);

        return new SuccessResponse<>(inquiries, REPORT_GET_SUCCESS.getMessage());
    }

    @PatchMapping("/inquiry/{inquiryId}")
    public SuccessResponse<?> answerInquiry(
            @CurrentEmail String email,
            @PathVariable(name = "inquiryId") Long inquiryId,
            @RequestBody InquiryAnswerReqDto reqDto
    ) {

        adminService.answerInquiry(email, inquiryId, reqDto);

        return new SuccessResponse<>(INQUIRY_ANSWER_SUCCESS.getMessage());
    }

    @PatchMapping("/report/{reportId}")
    public SuccessResponse updateReportStatus(
            @PathVariable(name = "reportId") Long reportId,
            @RequestParam(name = "reportStatus") ReportStatus status
    ) {
        adminService.updateReportStatus(reportId, status);

        return new SuccessResponse(REPORT_UPDATE_SUCCESS.getMessage());
    }

    @PatchMapping("/member/{memberId}")
    public SuccessResponse<?> updateMemberApprovedStatus(
            @PathVariable(name = "memberId") Long memberId,
            @RequestParam(name = "approvedStatus") ApprovedStatus approvedStatus) {

        adminService.updateApprovedStatus(memberId, approvedStatus);
        return new SuccessResponse<>(MEMBER_APPROVED_STATUS_UPDATE_SUCCESS.getMessage());
    }
}
