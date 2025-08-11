package com.souf.soufwebsite.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class EmitterRepository {

    private final Map<Long, List<SseEmitter>> store = new ConcurrentHashMap<>();

    public SseEmitter save(Long memberId, SseEmitter emitter) {
        store.computeIfAbsent(memberId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        return emitter;
    }

    public List<SseEmitter> findAll(Long memberId) {
        return store.getOrDefault(memberId, Collections.emptyList());
    }

    public void remove(Long memberId, SseEmitter emitter) {
        List<SseEmitter> emitters = store.get(memberId);
        if(emitters != null) {
            emitters.remove(emitter);
        }
    }
}
