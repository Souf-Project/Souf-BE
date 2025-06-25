package com.souf.soufwebsite.domain.chat.dto;

import java.time.LocalDateTime;

public record ChatRoomSummaryDto(
        Long roomId,
        String opponentNickname,
        String opponentProfileImageUrl,
        String lastMessage,
        LocalDateTime lastMessageTime,
        int unreadCount
) {
}
