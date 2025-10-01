package com.souf.soufwebsite.domain.inquiry.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquirySuccessMessage {

    INQUIRY_CREATE("피드를 생성하였습니다."),
    INQUIRY_FILE_METADATA_CREATE("피드 관련 파일을 업로드하였습니다."),
    INQUIRY_GET("피드를 조회하였습니다."),
    INQUIRY_UPDATE("피드를 수정하였습니다."),
    INQUIRY_DELETE("피드를 삭제하였습니다.");

    private final String message;
}
