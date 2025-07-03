package com.souf.soufwebsite.domain.file.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileUploadSuccessMessage {

    CREATE_VIDEO_PRESIGNED_URL("다음 분할 업로드를 위한 presignedURL을 발급하였습니다."),
    COMPLETE_UPLOAD_VIDEO("영상 파일을 성공적으로 업로드하였습니다.");

    private String message;
}
