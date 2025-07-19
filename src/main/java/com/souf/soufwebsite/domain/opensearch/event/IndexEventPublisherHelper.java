package com.souf.soufwebsite.domain.opensearch.event;

import com.souf.soufwebsite.domain.opensearch.EntityType;
import com.souf.soufwebsite.domain.opensearch.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component  // 스프링 빈으로 등록
@RequiredArgsConstructor
public class IndexEventPublisherHelper {

    private final ApplicationEventPublisher publisher;

    public void publishIndexEvent(EntityType entityType, OperationType operation, String payloadType, Object payload) {
        publisher.publishEvent(new IndexEvent(entityType, operation, payloadType, payload));
    }
}