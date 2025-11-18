package com.souf.soufwebsite.domain.feed.event;

public record FeedChangedEvent(Long id, ChangeType type) {
    public enum ChangeType {
        CONTENT_UPDATED,
        DELETED
    }
}