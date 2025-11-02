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

    private final Map<String, List<SseEmitter>> store = new ConcurrentHashMap<>();

    public SseEmitter save(String email, SseEmitter emitter) {
        store.computeIfAbsent(email, k -> new CopyOnWriteArrayList<>()).add(emitter);
        return emitter;
    }

    public List<SseEmitter> findAll(String email) {
        return store.getOrDefault(email, Collections.emptyList());
    }

    public void remove(String email, SseEmitter emitter) {
        List<SseEmitter> emitters = store.get(email);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) store.remove(email);
        }
    }

    public void removeAll(String email) { store.remove(email); }
}