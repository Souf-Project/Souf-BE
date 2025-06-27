package com.souf.soufwebsite.domain.member.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FavoriteMemberSuccessResponse {

    ADD_FAVORITE_SUCCESS("해당 회원을 즐겨찾기에 추가하였습니다."),
    GET_FAVORITE_SUCCESS("즐겨찾기 목록을 조회하였습니다.");

    private final String message;
}
