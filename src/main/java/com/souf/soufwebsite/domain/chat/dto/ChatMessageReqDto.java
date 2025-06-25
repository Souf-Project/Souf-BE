package com.souf.soufwebsite.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatMessageReqDto(
        @Schema(description="채팅방 ID", example="1")
        Long roomId,
        @Schema(description="메시지 타입", example="ENTER, TALK, LEAVE")
        MessageType type,
        @Schema(description="메시지 내용", example="안녕하세요.")
        String content
) {
}