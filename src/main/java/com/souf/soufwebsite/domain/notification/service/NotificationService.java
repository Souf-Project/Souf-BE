package com.souf.soufwebsite.domain.notification.service;

import com.souf.soufwebsite.domain.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(String email) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        emitterRepository.save(email, emitter);

        try {
            emitter.send(SseEmitter.event().name("init").data("SSE 연결 성공"));
        } catch (Exception e) {
            log.warn("SSE 초기 전송 실패: {}", e.getMessage());
        }

        emitter.onCompletion(() -> emitterRepository.remove(email, emitter));
        emitter.onTimeout(() -> emitterRepository.remove(email, emitter));
        emitter.onError(ex -> emitterRepository.remove(email, emitter));

        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    try { emitter.send(SseEmitter.event().name("heartbeat").data("")); }
                    catch (Exception ex) { emitterRepository.remove(email, emitter); }
                }, 15, 15, TimeUnit.SECONDS);

        log.info("SSE 구독 등록: {}", email);
        return emitter;
    }
}
