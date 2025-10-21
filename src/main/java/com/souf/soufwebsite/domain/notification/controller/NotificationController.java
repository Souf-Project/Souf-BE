package com.souf.soufwebsite.domain.notification.controller;

import com.souf.soufwebsite.domain.notification.service.NotificationService;
import com.souf.soufwebsite.global.util.CurrentEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@CurrentEmail String email) {
        SseEmitter emitter = notificationService.subscribe(email);
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }
}