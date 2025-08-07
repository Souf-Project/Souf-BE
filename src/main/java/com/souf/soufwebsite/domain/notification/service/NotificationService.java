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

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitterRepository.save(memberId, emitter);

        // 초기 연결 확인용 더미 이벤트
        try {
            emitter.send(SseEmitter.event()
                    .name("init")
                    .data("SSE 연결 성공"));
        } catch (Exception ignored) { }

        // 리소스 정리 핸들러
        emitter.onCompletion(() -> emitterRepository.remove(memberId, emitter));
        emitter.onTimeout   (() -> emitterRepository.remove(memberId, emitter));
        emitter.onError     ((ex) -> emitterRepository.remove(memberId, emitter));

        // 15초마다 heartbeat
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(() -> {
                    try {
                        emitter.send(SseEmitter.event().name("heartbeat").data(""));
                    } catch (Exception ex) {
                        // onError → delete 처리
                    }
                }, 15, 15, TimeUnit.SECONDS);

        return emitter;
    }
}
