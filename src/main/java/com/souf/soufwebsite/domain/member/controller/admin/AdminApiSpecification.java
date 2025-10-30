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
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "관리자 페이지", description = "각종 데이터들을 관리합니다.")
public interface AdminApiSpecification {

    @Operation(summary = "게시글 관리", description = "게시글 리스트를 조회합니다.")
    @GetMapping("/post")
    SuccessResponse<Page<AdminPostResDto>> getPosts(
            @RequestParam(name = "postType") PostType postType,
            @RequestParam(name = "writer", required = false) String writer,
            @RequestParam(name = "title", required = false) String title,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "회원 관리", description = "회원 리스트를 조회합니다.")
    @GetMapping("/member")
    SuccessResponse<Page<AdminMemberResDto>> getMembers(
            @RequestParam(name = "memberType") RoleType memberType,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "nickname", required = false) String nickname,
            @RequestParam(name = "approvedStatus", required = false) ApprovedStatus approvedStatus,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "신고 관리", description = "신고 리스트를 조회합니다.")
    @GetMapping("/report")
    SuccessResponse<Page<AdminReportResDto>> getReports(
            @RequestParam(name = "postType") PostType postType,
            @RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate,
            @RequestParam(name = "nickname") String nickname,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "문의 관리", description = "문의 리스트를 조회합니다.")
    @GetMapping("/inquiry")
    SuccessResponse<Page<InquiryResDto>> getInquiries(
            @RequestParam(name = "inquiryType", required = false)InquiryType inquiryType,
            @RequestParam(name = "inquiryStatus", required = false) InquiryStatus inquiryStatus,
            @RequestParam(name = "search", required = false) String search,
            @PageableDefault Pageable pageable
    );

    @PatchMapping("/inquiry/{inquiryId}")
    SuccessResponse<?> answerInquiry(
            @CurrentEmail String email,
            @PathVariable(name = "inquiryId") Long inquiryId,
            @RequestBody InquiryAnswerReqDto reqDto
    );

    @Operation(summary = "신고 처리", description = "관리자가 신고 게시글에 대한 처리를 결정합니다.")
    @PatchMapping("/report/{reportId}")
    SuccessResponse<?> updateReportStatus(
            @PathVariable(name = "reportId") Long reportId,
            @RequestParam(name = "reportStatus") ReportStatus status
    );

    @Operation(summary = "승인 처리", description = "인증 요청을 보낸 사용자에 대한 서비스 사용 여부를 결정합니다.")
    @PatchMapping("/member/{memberId}")
    SuccessResponse<?> updateMemberApprovedStatus(
            @PathVariable(name = "memberId") Long memberId,
            @RequestParam(name = "approvedStatus") ApprovedStatus approvedStatus
    );

}
//  신고 제재는 일주일, 15일, 탈퇴
// 조회는 싹다 풀기
