package com.souf.soufwebsite.global.jwt.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorKey {

    TOKEN_INVALID(401, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(401, "만료된 토큰입니다."),
    TOKEN_BLACKLISTED(401, "이미 로그아웃된 토큰입니다."),
    MEMBER_NOT_FOUND(401, "인증 사용자를 찾을 수 없습니다."),
    MEMBER_WITHDRAWN(401, "탈퇴한 사용자입니다."),

    MEMBER_BANNED(403, "이용이 제한된 사용자입니다.");

    private final int httpStatus;
    private final String message;
}