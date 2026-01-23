package com.souf.soufwebsite.domain.notification.outbox;


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
    private String dedupKey;

    @Lob
    @Column(name = "payload_json", nullable = false)
    private String payloadJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationOutboxStatus status;

    @Column(nullable = false)
    private int retryCount;

    @Column(nullable = false)
    private int maxRetries;

    @Column(nullable = false)
    private LocalDateTime nextRetryAt;

    private LocalDateTime sentAt;

    @Column(length = 500)
    private String lastError;

    @Builder
    private NotificationOutbox(String dedupKey, String payloadJson, int maxRetries, LocalDateTime nextRetryAt) {
        this.dedupKey = dedupKey;
        this.payloadJson = payloadJson;
        this.status = NotificationOutboxStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = maxRetries;
        this.nextRetryAt = nextRetryAt;
    }

    public void markProcessing() {
        this.status = NotificationOutboxStatus.PROCESSING;
    }

    public void markSent() {
        this.status = NotificationOutboxStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.lastError = null;
    }

    public void markFailed(String err) {
        this.retryCount += 1;
        this.lastError = truncate(err, 500);

        if (this.retryCount >= this.maxRetries) {
            this.status = NotificationOutboxStatus.FAILED;
        } else {
            this.status = NotificationOutboxStatus.PENDING;
            this.nextRetryAt = LocalDateTime.now().plusSeconds(backoffSeconds(this.retryCount));
        }
    }

    private static long backoffSeconds(int retryCount) {
        // 1회 10s, 2회 30s, 3회 60s, 4회 120s, 5회 300s ... (상한 10분)
        long[] steps = {10, 30, 60, 120, 300, 600};
        return steps[Math.min(retryCount - 1, steps.length - 1)];
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max);
    }
}
