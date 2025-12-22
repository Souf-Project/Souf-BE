package com.souf.soufwebsite.domain.notification.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationOutbox {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dedup_key", nullable = false, length = 200)
    private String dedupKey; // ex) APPLICATION_REVIEWED:APPLICATION:123

    @Lob
    @Column(name = "payload", nullable = false)
    private String payloadJson; // NotificationDto를 JSON으로 직렬화한 값

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxStatus status;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private int maxRetries;

    @Column(nullable = false)
    private LocalDateTime nextRetryAt;

    private LocalDateTime sentAt;

    @Column(length = 500)
    private String lastError;

    @Version
    private Long version;

    @Builder
    private NotificationOutbox(String dedupKey, String payloadJson, int maxRetries, LocalDateTime nextRetryAt) {
        this.dedupKey = dedupKey;
        this.payloadJson = payloadJson;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = maxRetries;
        this.nextRetryAt = nextRetryAt;
    }

    public void markSent() {
        this.status = OutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.lastError = null;
    }

    public void markFailed(String err) {
        this.retryCount += 1;
        this.lastError = truncate(err, 500);

        if (this.retryCount >= this.maxRetries) {
            this.status = OutboxStatus.FAILED;
        } else {
            this.status = OutboxStatus.PENDING;
            this.nextRetryAt = LocalDateTime.now().plusSeconds(backoffSeconds(this.retryCount));
        }
    }

    private static long backoffSeconds(int retryCount) {
        // 1회 10s, 2회 30s, 3회 60s, 4회 120s ... (상한 10분)
        long[] steps = {10, 30, 60, 120, 300, 600};
        return steps[Math.min(retryCount - 1, steps.length - 1)];
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
