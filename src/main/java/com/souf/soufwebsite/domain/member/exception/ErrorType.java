package com.souf.soufwebsite.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_AVAILABLE_EMAIL(400, "이미 존재하는 이메일입니다."),
    NOT_MATCH_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND_MEMBER(404, "해당 사용자를 찾을 수 없습니다."),
    NOT_VERIFIED_EMAIL(400, "이메일 인증이 완료되지 않았습니다.");

    private final int code;
    private final String message;
}
