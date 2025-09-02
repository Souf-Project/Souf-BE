package com.souf.soufwebsite.domain.socialAccount.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_VALID_AUTHENTICATION(403, "해당 소셜 계정으로 로그인할 수 없습니다"),
    DUPLICATE_EMAIL(409, "이미 가입된 이메일입니다. 마이페이지에서 소셜 계정을 연결해주세요.");

    private final int code;
    private final String message;
}
