package com.souf.soufwebsite.domain.notification.dto;

import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationDto(
        Long targetMemberId,       // 알림 수신자 ID
        NotificationType type,     // 알림 종류
        String title,              // 제목 (헤더 종표시용)
        String body,               // 내용 (알림 상세)
        String refType,            // 참조 도메인: RECRUIT, INQUIRY, RECRUIT_CATEGORY 등
        Long refId,                // 참조 PK
        LocalDateTime createdAt,   // 생성 시각

        // ====== 메일 전송 관련 선택 필드 ======
        String email,              // 수신자 이메일
        String nickname,           // 수신자 닉네임
        String recruitTitle        // (선택) 공고 제목 등
) {
    // 선택: 빌더 스타일 유틸리티 제공
    public static NotificationDto ofBasic(
            Long targetMemberId,
            NotificationType type,
            String title,
            String body,
            String refType,
            Long refId
    ) {
        return new NotificationDto(targetMemberId, type, title, body, refType, refId, LocalDateTime.now(),
                null, null, null);
    }

    public static NotificationDto ofRecruitCreated(
            Long targetMemberId,
            String email,
            String nickname,
            String recruitTitle,
            String body,
            Long refId
    ) {
        return new NotificationDto(targetMemberId, NotificationType.APPLICANT_CREATED,
                "새 지원자 발생", body, "RECRUIT", refId, LocalDateTime.now(),
                email, nickname, recruitTitle);
    }

    public static NotificationDto ofInquiryReplied(
            Long targetMemberId,
            String email,
            String nickname,
            String body,
            Long inquiryId
    ) {
        return new NotificationDto(targetMemberId, NotificationType.INQUIRY_REPLIED,
                "문의에 답변이 등록됐어요", body, "INQUIRY", inquiryId, LocalDateTime.now(),
                email, nickname, null);
    }
}
