package com.souf.soufwebsite.domain.member.dto.ResDto;

import com.souf.soufwebsite.domain.report.entity.ReportStatus;

import java.time.LocalDateTime;

public record AdminReportResDto(

        Long reportId,

        Long postId,
        String postType,
        String postTitle, // 게시글 제목(댓글은 내용으로 반환)

        Long reportedPersonId,
        String reportedPersonNickname, // 신고당한 사용자 닉네임
        Long reportingPersonId,
        String reportingPersonNickname, // 신고한 사용자 닉네임
        LocalDateTime reportedDate,
        Long reasonId,
        String description,
        ReportStatus status

) {
}
