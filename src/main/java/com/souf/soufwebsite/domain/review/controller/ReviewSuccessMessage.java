package com.souf.soufwebsite.domain.review.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewSuccessMessage {

    REVIEW_CREATE("후기를 생성하였습니다."),
    REVIEW_FILE_METADATA_CREATE("후기 관련 파일을 업로드하였습니다."),
    REVIEW_GET("후기를 조회하였습니다."),
    REVIEW_GET_POPULATION("인기있는 후기를 조회하였습니다."),
    REVIEW_UPDATE("후기를 수정하였습니다."),
    REVIEW_DELETE("후기를 삭제하였습니다.");

    private final String message;
}
