package com.souf.soufwebsite.domain.chat.dto;

public record ChatMessageResDto(
        Long roomId,
        String sender,
        MessageType type,
        String content,
        boolean isRead
) {
}