package com.souf.soufwebsite.domain.recruit.event;

import java.time.LocalDateTime;

public record RecruitChangedEvent(Long id, ChangeType type, boolean recruitable, LocalDateTime deadline) {
    public enum ChangeType {
        CREATED,
        CONTENT_UPDATED,
        STATUS_OR_DEADLINE_UPDATED,
        DELETED }
}