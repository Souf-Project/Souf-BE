package com.souf.soufwebsite.domain.notification.service;

import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.notification.dto.NotificationDto;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationPublisher notificationPublisher;

    public void notify(
            Member target,
            NotificationType type,
            String title,
            String body,
            String refType,
            Long refId
    ) {
        if (target == null || target.getId() == null || target.getEmail() == null) return;

        NotificationDto dto = new NotificationDto(
                target.getEmail(),
                target.getId(),
                type,
                title,
                body,
                refType,
                refId,
                LocalDateTime.now()
        );

        notificationPublisher.publish(dto);
    }
}