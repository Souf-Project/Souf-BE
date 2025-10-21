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
    private final SesMailService sesMailService;

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

        // 2) 메일 발송 (채팅 제외 이벤트)
        try {
            if (dto.email() == null) return;
            if (dto.type() == NotificationType.APPLICANT_CREATED) {
                sesMailService.announceRecruitResult(dto.email(), dto.nickname(), dto.recruitTitle());
            } else if (dto.type() == NotificationType.INQUIRY_REPLIED) {
                sesMailService.sendEmailAuthenticationCode(dto.email(), "문의 답변 안내", "답변이 등록되었습니다.");
            }
            // RECRUIT_PUBLISHED: 묶음 SSE만, 메일 X
        } catch (Exception ignore) {
            // 메일 실패는 무시 (로그만 남기고 알림 흐름 유지)
        }
    }
}