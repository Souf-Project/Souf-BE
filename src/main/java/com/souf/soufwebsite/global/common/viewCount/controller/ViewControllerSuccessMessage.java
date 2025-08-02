package com.souf.soufwebsite.global.common.viewCount.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ViewControllerSuccessMessage {

    GET_COUNT_SUCCESS("회원 수 조회에 성공하였습니다!");

    final String msg;
}
