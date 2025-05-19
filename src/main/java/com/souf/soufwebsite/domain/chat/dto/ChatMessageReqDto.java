package com.souf.soufwebsite.domain.chat.dto;

public record ChatMessageReqDto(
        Long roomId,
        MessageType type,
        String content
) {
}