package com.souf.soufwebsite.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.notification.dto.NotificationDto;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import com.souf.soufwebsite.domain.notification.event.NotificationEvent;
import com.souf.soufwebsite.domain.notification.outbox.NotificationOutbox;
import com.souf.soufwebsite.domain.notification.outbox.NotificationOutboxPayload;
import com.souf.soufwebsite.domain.notification.outbox.NotificationOutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationFacade {

    private final NotificationService notificationService;
    private final NotificationPublisher notificationPublisher;

    private final NotificationOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    /** AFTER_COMMIT에서 호출되므로 반드시 새 트랜잭션으로 적재 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void enqueueAfterCommit(NotificationEvent e) {
        enqueue(
                e.targetMemberId(),
                e.type(),
                e.title(),
                e.body(),
                e.refType(),
                e.refId(),
                e.dedupKey()
        );
    }

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
        } catch (Exception ex) {
            throw new IllegalStateException("notification outbox payload serialize failed", ex);
        }

        String key = (dedupKey != null && !dedupKey.isBlank())
                ? dedupKey
                : type + ":" + refType + ":" + refId + ":" + targetMemberId;

        try {
            NotificationOutbox saved = outboxRepository.saveAndFlush(NotificationOutbox.builder()
                    .dedupKey(key)
                    .payloadJson(json)
                    .maxRetries(5)
                    .nextRetryAt(LocalDateTime.now())
                    .build());

            log.info("[outbox] saved id={}, dedupKey={}", saved.getId(), key);

        } catch (DataIntegrityViolationException e) {
            if (isDuplicateDedupKey(e)) {
                log.info("[outbox] duplicate ignored, dedupKey={}", key);
                return;
            }
            log.error("[outbox] save failed, dedupKey={}", key, e);
            throw e;
        }
    }

    private boolean isDuplicateDedupKey(DataIntegrityViolationException e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof ConstraintViolationException cve) {
                String name = cve.getConstraintName();
                if (name != null && name.toLowerCase().contains("dedup")) return true;
            }
            t = t.getCause();
        }
        String msg = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
        return msg != null && msg.toLowerCase().contains("duplicate");
    }

    @Transactional
    public void sendNow(Member target,
                        NotificationType type,
                        String title,
                        String body,
                        String refType,
                        Long refId) {

        log.info("sendNow called: memberId={}, type={}, refType={}, refId={}",
                target != null ? target.getId() : null,
                type,
                refType,
                refId);
        if (target == null || target.getId() == null || target.getEmail() == null) return;

        NotificationDto dto = new NotificationDto(
                target.getEmail(),
                target.getId(),
                type,
                title,
                body,
                refType,
                refId,
                java.time.LocalDateTime.now()
        );

        notificationPublisher.publish(dto);
    }
}