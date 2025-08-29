package com.souf.soufwebsite.domain.member.controller.admin;

import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminMemberResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminPostResDto;
import com.souf.soufwebsite.domain.member.dto.ResDto.AdminReportResDto;
import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.member.service.admin.AdminService;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import com.souf.soufwebsite.global.success.SuccessResponse;
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

    @PatchMapping("/report/{reportId}")
    public SuccessResponse updateReportStatus(
            @PathVariable(name = "reportId") Long reportId,
            @RequestParam(name = "reportStatus") ReportStatus status
    ) {
        adminService.updateReportStatus(reportId, status);

        return new SuccessResponse(REPORT_UPDATE_SUCCESS.getMessage());
    }
}
