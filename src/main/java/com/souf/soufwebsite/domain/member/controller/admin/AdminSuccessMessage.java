package com.souf.soufwebsite.domain.member.controller.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminSuccessMessage {

    POST_GET_SUCCESS("관리할 게시글들을 조회하였습니다."),
    MEMBER_GET_SUCCESS("관리할 회원들을 조회하였습니다."),
    REPORT_GET_SUCCESS("신고 목록들을 조회하였습니다.");

    private final String message;
}
