package com.souf.soufwebsite.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatMessageResDto(
        Long roomId,
        Long chatId,
        String sender,
        MessageType type,
        String content,
        boolean isRead,
        LocalDateTime createdTime
        ) {
}