package com.souf.soufwebsite.domain.notification.entity;

public enum NotificationType {
    APPLICANT_CREATED,   // 새 지원자 발생 → 작성자에게 즉시 SSE + 메일
    INQUIRY_REPLIED,     // 문의 답변 등록 → 작성자에게 즉시 SSE + 메일
    RECRUIT_PUBLISHED    // 관심 카테고리 공고 등록 → 1시간 묶음 SSE (메일 X)
}