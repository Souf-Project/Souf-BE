package com.souf.soufwebsite.domain.feed.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    // FEED
    NOT_VALID_AUTHENTICATION(403, "해당 피드를 수정할 권한이 없습니다.", "F403-1"),
    NOT_FOUND_FEED(404, "해당 피드를 찾을 수 없습니다.", "F404-1"),

    // LIKE_FEED
    ALREADY_EXISTS_LIKE(400, "이미 좋아요를 눌렀습니다.", "F400-1"),
    NOT_EXISTS_LIKE(404, "좋아요를 누르지 않았습니다.", "F404-2"),
    NOT_FOUND_FEED_LIKE(400, "해당 회원의 좋아요 여부를 확인할 수 없습니다.", "F400-2");


    private final int code;
    private final String message;
    private final String errorKey;
}