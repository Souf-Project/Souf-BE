package com.souf.soufwebsite.domain.chat.dto;

public record ChatRoomSummaryDto(
        Long roomId,
        String senderNickname,
        String lastMessage,
        int unreadCount
) {
}
