package com.souf.soufwebsite.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import com.souf.soufwebsite.domain.notification.outbox.NotificationOutbox;
import com.souf.soufwebsite.domain.notification.outbox.NotificationOutboxPayload;
import com.souf.soufwebsite.domain.notification.outbox.NotificationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void enqueue(
            Long targetMemberId,
            NotificationType type,
            String title,
            String body,
            String refType,
            Long refId,
            String dedupKey
    ) {
        NotificationOutboxPayload payload = new NotificationOutboxPayload(
                targetMemberId, type, title, body, refType, refId
        );

        final String json;
        try {
            json = objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            throw new IllegalStateException("notification outbox payload serialize failed", e);
        }

        String key = (dedupKey != null && !dedupKey.isBlank())
                ? dedupKey
                : type + ":" + refType + ":" + refId + ":" + targetMemberId;

        try {
            outboxRepository.save(NotificationOutbox.builder()
                    .dedupKey(key)
                    .payloadJson(json)
                    .maxRetries(5)
                    .nextRetryAt(LocalDateTime.now())
                    .build());
        } catch (DataIntegrityViolationException dup) {
            // ✅ 중복 적재 방지 (재시도/중복 호출 대비)
        }
    }

    // ✅ “실제 발송” (기존 notificationFacade.notify(...)가 하던 일을 여기로 이동)
    // - Notification 엔티티 저장
    // - SSE / MQ publish 등
    @Transactional
    public void sendNow(Member target,
                        NotificationType type,
                        String title,
                        String body,
                        String refType,
                        Long refId) {
        // TODO: 여기 안에 기존 notify 로직(알림 저장 + publish)을 옮기면 됨
        // 예) notificationService.create(target, type, title, body, refType, refId);
        // 예) notificationPublisher.publish(...)
    }
}