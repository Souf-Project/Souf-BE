package com.souf.soufwebsite.domain.notification.outbox;


import com.souf.soufwebsite.domain.notification.entity.NotificationType;

public record NotificationOutboxPayload(
        Long targetMemberId,
        NotificationType type,
        String title,
        String body,
        String refType,
        Long refId
) {}