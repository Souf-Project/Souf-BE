package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.file.entity.PostType;
import com.souf.soufwebsite.domain.report.entity.ReportReason;
import com.souf.soufwebsite.domain.report.entity.ReportStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record AdminReportResDto(

        @Schema(description = "신고 아이디", example = "1")
        Long reportId,

        @Schema(description = "게시글 아이디", example = "1")
        Long postId,
        @Schema(description = "게시글 타입", example = "FEED")
        PostType postType,
        @Schema(description = "게시글 제목", example = "게시글 제목")
        String postTitle, // 게시글 제목(댓글은 내용으로 반환)

        @Schema(description = "신고받은 사용자 아이디", example = "1")
        Long reportedPersonId,
        @Schema(description = "신고받은 사용자 닉네임", example = "userNickname")
        String reportedPersonNickname, // 신고당한 사용자 닉네인
        @Schema(description = "신고한 사용자 아이디", example = "1")
        Long reportingPersonId,
        @Schema(description = "신고한 사용자 닉네임", example = "minarinamu")
        String reportingPersonNickname, // 신고한 사용자 닉네
        @Schema(description = "신고 날짜", example = "2025-08-20'T'23:15:00")
        LocalDateTime reportedDate,
        @Schema(description = "신고 사유", example = "VIOLENT")
        ReportReason reportReason,
        @Schema(description = "신고 상세 설명", example = "이 사람 너무 이상해요")
        String description,
        @Schema(description = "신고 처리 상태", example = "REVIEWING")
        ReportStatus status

) {
}
