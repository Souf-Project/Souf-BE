package com.souf.soufwebsite.domain.notification.service;

import com.souf.soufwebsite.domain.notification.dto.NotificationDto;
import com.souf.soufwebsite.domain.notification.entity.NotificationType;
import com.souf.soufwebsite.domain.notification.repository.EmitterRepository;
import com.souf.soufwebsite.global.common.mail.SesMailService;
import com.souf.soufwebsite.global.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmitterRepository emitterRepository;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receive(NotificationDto dto) {
        // 1) SSE push (email 기준)
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