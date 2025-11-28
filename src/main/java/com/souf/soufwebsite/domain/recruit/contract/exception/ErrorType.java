package com.souf.soufwebsite.domain.recruit.contract.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {

    /* ======================================= 400 ================================================= */

    NOT_EXISTS_INVITE_TOKEN(400, "쿠키에 초대 토큰이 존재하지 않습니다.", "CO400-1"),

    /* ======================================= 403 ================================================= */

    NOT_ACCEPTED_MEMBER(403, "지정 사용자만 사용할 수 있는 토큰입니다.", "CO403-1"),
    NOT_ACCEPTED_CHATROOM(403, "지정 채팅방에서만 사용할 수 있는 토큰입니다.", "CO403-2"),
    NOT_ACCEPTED_CONTRACT(403, "계약번호가 달라 계약서에 접근할 수 없습니다.", " CO403-3"),

    /* ======================================= 404 ================================================= */

    NOT_FOUND_CONTRACT(404, "해당 계약서가 존재하지 않습니다.", "CO404-1"),
    NOT_FOUND_CONTRACT_INVITE(404, "계약서 작성 초대 코드가 유효하지 않습니다.", "CO404-2"),

    /* ====================================== 409 ================================================= */

    ALREADY_SIGNED_CONTRACT(409, "이미 서명 완료된 계약서입니다.", "CO409-1"),
    REVOKED_INVITE_TOKEN(409, "취소된 토큰입니다.", "CO409-2"),
    ALREADY_EXISTS_PROGRESSING_CONTRACT(409, "이미 진행 중인 계약이 존재합니다.", " CO409-3"),

    /* ====================================== 410 ================================================ */

    ALREADY_EXPIRED_INVITE_TOKEN(410, "이미 만료된 초대 코드입니다.", "CO410-1"),
    ALREADY_CONSUMED_INVITE_TOKEN(410, "이미 사용이 끝난 초대 코드입니다.",  "CO410-2");


    private final int code;
    private final String message;
    private final String errorKey;
}
