package com.souf.soufwebsite.domain.member.controller.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminSuccessMessage {

    POST_GET_SUCCESS("관리할 게시글들을 조회하였습니다."),
    MEMBER_GET_SUCCESS("관리할 회원들을 조회하였습니다."),
    REPORT_GET_SUCCESS("신고 목록들을 조회하였습니다."),

    REPORT_UPDATE_SUCCESS("제재가 성공적으로 완료되었습니다."),

    INQUIRY_ANSWER_SUCCESS("문의에 성공적으로 답변하였습니다."),

    MEMBER_APPROVED_STATUS_UPDATE_SUCCESS("회원 승인 처리가 성공적으로 처리되었습니다.");

    private final String message;
}
