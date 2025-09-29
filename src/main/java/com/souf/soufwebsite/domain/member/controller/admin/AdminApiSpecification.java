package com.souf.soufwebsite.domain.member.controller.admin;

import com.souf.soufwebsite.domain.member.entity.RoleType;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Tag(name = "관리자 페이지", description = "각종 데이터들을 관리합니다.")
public interface AdminApiSpecification {

    @Operation(summary = "게시글 관리", description = "게시글 리스트를 조회합니다.")
    @GetMapping("/post")
    SuccessResponse<?> getPosts(
            @RequestParam(name = "postType") PostType postType,
            @RequestParam(name = "writer", required = false) String writer,
            @RequestParam(name = "title", required = false) String title,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "회원 관리", description = "회원 리스트를 조회합니다.")
    @GetMapping("/member")
    SuccessResponse<?> getMembers(
            @RequestParam(name = "memberType") RoleType memberType,
            @RequestParam(name = "username", required = false) String username,
            @RequestParam(name = "nickname", required = false) String nickname,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "신고 관리", description = "신고 리스트를 조회합니다.")
    @GetMapping("/report")
    SuccessResponse<?> getReports(
            @RequestParam(name = "postType") PostType postType,
            @RequestParam(name = "startDate") LocalDate startDate,
            @RequestParam(name = "endDate") LocalDate endDate,
            @RequestParam(name = "nickname") String nickname,
            @PageableDefault Pageable pageable
    );

    @Operation(summary = "신고 처리", description = "관리자가 신고 게시글에 대한 처리를 결정합니다.")
    @PatchMapping("/report/{reportId}")
    SuccessResponse<?> updateReportStatus(
            @PathVariable(name = "reportId") Long reportId,
            @RequestParam(name = "reportStatus") ReportStatus status
    );

}
//  신고 제재는 일주일, 15일, 탈퇴
// 조회는 싹다 풀기
