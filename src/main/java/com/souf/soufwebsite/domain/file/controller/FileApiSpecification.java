package com.souf.soufwebsite.domain.file.controller;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.S3VideoUploadSignedUrlReqDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoUploadCompletedDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "File", description = "미디어 파일 업로드 관련 API")
public interface FileApiSpecification {

    @Operation(summary = "동영상 분할 업로드", description = "동영상 파일을 분할 업로드하기 위해 서버에 지속적으로 PresignedUrl을 요청합니다.")
    @PostMapping("/upload-signed-url")
    SuccessResponse<PresignedUrlResDto> uploadSignedUrl(@RequestBody S3VideoUploadSignedUrlReqDto uploadSignedUrlReqDto);

    @Operation(summary = "동영상 업로드 완료 송신", description = "동영상의 업로드를 모두 마쳤다면 동영상을 합치기 위한 신호를 보냅니다.")
    @PostMapping("/complete-video-upload")
    SuccessResponse completeVideoUpload(@RequestBody VideoUploadCompletedDto videoUploadCompletedDto);
}
