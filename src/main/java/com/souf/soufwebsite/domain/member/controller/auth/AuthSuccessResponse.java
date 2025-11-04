package com.souf.soufwebsite.domain.member.controller.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthSuccessResponse {

    REISSUE_TOKEN_SUCCESS("토큰 재발급에 성공하였습니다.");

    private final String message;
}
