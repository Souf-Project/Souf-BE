package com.souf.soufwebsite.domain.socialAccount.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_VALID_AUTHENTICATION(403, "해당 소셜 계정으로 로그인할 수 없습니다"),
    DUPLICATE_EMAIL(409, "이미 가입된 이메일입니다. 마이페이지에서 소셜 계정을 연결해주세요."),
    ALREADY_LINKED_OTHER_USER(409, "이미 다른 사용자에 연결된 소셜 계정입니다."),
    ALREADY_LINKED(409, "이미 해당 소셜 제공자가 연결되어 있습니다.");

    private final int code;
    private final String message;
}
