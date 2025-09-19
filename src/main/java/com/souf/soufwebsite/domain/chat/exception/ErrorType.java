package com.souf.soufwebsite.domain.chat.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    NOT_FOUND_CHAT_MESSAGE(404, "해당 채팅을 찾을 수 없습니다.", "CH404-1"),
    NOT_CHAT_MYSELF(400, "자신과의 채팅은 생성할 수 없습니다.", "CH400-1"),
    NOT_FOUND_CHAT_ROOM(404, "해당 채팅방을 찾을 수 없습니다.", "CH404-2"),
    NOT_FOUND_PARTICIPANT(404, "해당 채팅방의 참여자를 찾을 수 없습니다.", "CH404-3"),
    NOT_CHAT_ROOM_PARTICIPANT(403, "채팅방 참여자가 아닙니다.", "CH403-1"),;

    private final int code;
    private final String message;
    private final String errorKey;
}