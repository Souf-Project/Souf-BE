package com.souf.soufwebsite.domain.notification.dto;

import com.souf.soufwebsite.domain.notification.entity.Notification;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationItemDto(
        Long id,
        NotificationType type,
        String title,
        String body,
        String refType,
        Long refId,
        LocalDateTime createdAt,
        boolean read
) {
    public static NotificationItemDto from(Notification n) {
        return new NotificationItemDto(
                n.getId(),
                n.getType(),
                n.getTitle(),
                n.getBody(),
                n.getRefType(),
                n.getRefId(),
                n.getCreatedTime(),
                n.isRead()
        );
    }
}