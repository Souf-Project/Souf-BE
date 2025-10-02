package com.souf.soufwebsite.domain.inquiry.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InquirySuccessMessage {

    INQUIRY_CREATE("문의글을 생성하였습니다."),
    INQUIRY_FILE_METADATA_CREATE("문의글에 포함된 파일을 업로드하였습니다."),
    INQUIRY_GET("문의글을 조회하였습니다."),
    INQUIRY_UPDATE("문의글을 수정하였습니다."),
    INQUIRY_DELETE("문의글을 삭제하였습니다.");

    private final String message;
}
