package com.souf.soufwebsite.domain.notification.dto;

import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationDto(
        String email,              // 알림 수신자 이메일 (SSE용)
        Long targetMemberId,       // 알림 수신자 ID
        NotificationType type,     // 알림 종류
        String title,              // 제목 (헤더 종표시용)
        String body,               // 내용 (알림 상세)
        String refType,            // 참조 도메인: RECRUIT, INQUIRY, RECRUIT_CATEGORY 등
        Long refId,                // 참조 PK
        String link,               // 알림 링크 (클릭 시 이동할 URL)
        LocalDateTime createdAt    // 생성 시각
) {
}
