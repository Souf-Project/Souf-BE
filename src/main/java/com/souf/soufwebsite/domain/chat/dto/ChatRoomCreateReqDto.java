package com.souf.soufwebsite.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ChatRoomCreateReqDto(
        @Schema(description="채팅 상대 ID", example="1")
        Long receiverId
) {
}
