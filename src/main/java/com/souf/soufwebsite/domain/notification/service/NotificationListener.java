package com.souf.soufwebsite.domain.notification.service;

import com.souf.soufwebsite.domain.notification.dto.NotificationDto;
import com.souf.soufwebsite.domain.notification.entity.Notification;
import com.souf.soufwebsite.domain.notification.repository.EmitterRepository;
import com.souf.soufwebsite.domain.notification.repository.NotificationRepository;
import com.souf.soufwebsite.global.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    @Transactional
    public void receive(NotificationDto dto) {
        notificationRepository.save(
                Notification.of(
                    dto.targetMemberId(),
                    dto.type(),
                    dto.title(),
                    dto.body(),
                    dto.refType(),
                    dto.refId()
                )
        );

        try {
            Long memberId = dto.targetMemberId();
            if (memberId != null) {
                long total = notificationRepository.countByMemberId(memberId);
                if (total > 50) {
                    notificationRepository.trimToMax(memberId, 50);
                }
            }
        } catch (Exception e) {
            log.warn("Notification retention trim failed", e);
        }

        String email = dto.email();
        if (email != null) {
            List<SseEmitter> emitters = emitterRepository.findAll(email);
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(UUID.randomUUID().toString())
                            .name("notification")
                            .reconnectTime(3000L)
                            .data(dto, MediaType.APPLICATION_JSON));
                } catch (IOException e) {
                    emitterRepository.remove(email, emitter);
                }
            }
        }
    }
}