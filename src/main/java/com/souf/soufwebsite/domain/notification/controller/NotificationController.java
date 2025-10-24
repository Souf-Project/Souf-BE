package com.souf.soufwebsite.domain.notification.controller;

import com.souf.soufwebsite.domain.notification.dto.NotificationItemDto;
import com.souf.soufwebsite.domain.notification.service.NotificationReadService;
import com.souf.soufwebsite.domain.notification.service.NotificationService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationReadService notificationReadService;

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@CurrentEmail String email) {
        SseEmitter emitter = notificationService.subscribe(email);
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }

    @GetMapping
    public SuccessResponse<Page<NotificationItemDto>> list(
            @CurrentEmail String email,
            @RequestParam(defaultValue = "0") int page
    ) {
        Pageable pageable = PageRequest.of(Math.max(0, page), 10, Sort.by(DESC, "createdTime"));
        return new SuccessResponse<>(notificationReadService.getMyNotifications(email, pageable));
    }

    @GetMapping("/unread-count")
    public SuccessResponse<Long> unreadCount(@CurrentEmail String email) {
        return new SuccessResponse<>(notificationReadService.getUnreadCount(email));
    }

    @DeleteMapping("/{notificationId}")
    public SuccessResponse<Void> deleteOne(
            @CurrentEmail String email,
            @PathVariable Long notificationId
    ) {
        notificationReadService.deleteOne(email, notificationId);
        return new SuccessResponse<>(null);
    }
}