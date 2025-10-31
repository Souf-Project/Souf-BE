package com.souf.soufwebsite.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    NOT_AVAILABLE_EMAIL(400, "이미 존재하는 이메일입니다.", "M400-1"),
    NOT_MATCH_PASSWORD(400, "비밀번호가 일치하지 않습니다.", "M400-2"),
    NOT_FOUND_MEMBER(404, "해당 사용자를 찾을 수 없습니다.", "M404-1"),
    NOT_VERIFIED_EMAIL(400, "이메일 인증이 완료되지 않았습니다.", "M400-3"),
    NOT_VALID_EMAIL(400, "유효하지 않은 이메일 형식입니다.", "M400-4"),
    NOT_VALID_ROLE_TYPE(400, "유효하지 않은 권한입니다.", "M400-8"),
    NOT_APPROVED_ACCOUNT(400, "인증되지 않은 유저입니다.", "M400-9"),

    // ----------------------------------- Favorite --------------------------------

    NOT_FOUND_FAVORITE(404, "즐겨찾기 목록에서 찾을 수 없습니다.", "M404-2"),

    // ----------------------------------- Agreement -------------------------------

    NOT_AGREED_PERSONAL_INFO(400, "개인정보 동의서에 동의하지 않았습니다.", "M400-5"),
    NOT_ALLOW_SIGNUP(400, "가입이 제한된 이메일입니다.", "M400-6"),

    // ----------------------------------- Club -------------------------------

    NOT_FOUND_CLUB(404, "해당 동아리를 찾을 수 없습니다.", "M404-3"),
    NOT_VALID_MANAGE_AUTHENTICATION(403, "동아리 관리 권한이 없습니다.", "M403-1"),
    NOT_VALID_AUTHENTICATION(403, "권한이 없는 사용자입니다.", "M403-2"),
    ALREADY_JOINED_CLUB(409, "이미 가입(신청)한 동아리입니다.", "M409-1"),
    INVALID_JOIN_DECISION(400, "동아리 가입 승인/거절이 유효하지 않습니다.", "M400-7"),
    NOT_FOUND_PENDING_APPLY(404, "대기중인 동아리 신청서를 찾을 수 없습니다.", "M404-4"),;

    private final int code;
    private final String message;
    private final String errorKey;
}
