package com.souf.soufwebsite.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.souf.soufwebsite.domain.notification.dto.NotificationDto;
import com.souf.soufwebsite.domain.notification.repository.EmitterRepository;
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
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void receive(NotificationDto dto) {

        List<SseEmitter> emitters = emitterRepository.findAll(dto.getTargetMemberId());
        if(emitters.isEmpty()) {
            return;
        }

        emitters.forEach(emitter -> {
            try {

                emitter.send(SseEmitter.event()
                        .id(UUID.randomUUID().toString())
                        .name("report-notification")
                        .reconnectTime(3_000L)
                        .data(objectMapper.writeValueAsString(dto), MediaType.APPLICATION_JSON));
            } catch (IOException e) {
                emitterRepository.remove(dto.getTargetMemberId(), emitter);
            }
        });
    }
}
