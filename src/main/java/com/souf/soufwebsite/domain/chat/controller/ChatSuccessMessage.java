package com.souf.soufwebsite.domain.chat.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatSuccessMessage {

    COUNT_UNREAD_MESSAGES("읽지 않은 채팅 메시지 수를 조회하였습니다.");

    private final String message;
}
