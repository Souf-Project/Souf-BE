package com.souf.soufwebsite.domain.opensearch.event;

import com.souf.soufwebsite.domain.opensearch.EntityType;
import com.souf.soufwebsite.domain.opensearch.OperationType;

public record IndexEvent(
        EntityType entityType,
        OperationType operation,
        String payloadType,
        Object payload
) {
}
