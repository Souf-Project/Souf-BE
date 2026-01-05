package com.souf.soufwebsite.domain.notification.event;

import com.souf.soufwebsite.domain.notification.entity.NotificationType;

public record NotificationEvent(
        Long targetMemberId,
        NotificationType type,
        String title,
        String body,
        String refType,
        Long refId,
        String dedupKey
) {
    public static String defaultDedupKey(NotificationEvent e) {
        return e.type() + ":" + e.refType() + ":" + e.refId() + ":" + e.targetMemberId();
    }
}