package com.souf.soufwebsite.global.success;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessResponseStatus {

    SUCCESS(200, "요청에 성공하였습니다!");

    private final int code;
    private final String message;
}
