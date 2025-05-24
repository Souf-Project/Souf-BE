package com.souf.soufwebsite.domain.recruit.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitSuccessMessage {

    RECRUIT_CREATE("공고문을 생성하였습니다."),
    RECRUIT_FILE_METADATA_CREATE("공고문 관련 파일을 업로드하였습니다."),
    RECRUIT_GET("공고문을 조회하였습니다."),
    RECRUIT_GET_POPULATION("인기있는 공고문을 조회하였습니다."),
    RECRUIT_UPDATE("공고문을 수정하였습니다."),
    RECRUIT_DELETE("공고문을 삭제하였습니다.");

    private final String message;
}
