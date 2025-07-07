package com.souf.soufwebsite.domain.file.controller;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.S3VideoUploadSignedUrlReqDto;
import com.souf.soufwebsite.domain.file.dto.VideoUploadCompletedDto;
import com.souf.soufwebsite.domain.file.service.S3UploaderService;
import com.souf.soufwebsite.global.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.souf.soufwebsite.domain.file.controller.FileUploadSuccessMessage.COMPLETE_UPLOAD_VIDEO;
import static com.souf.soufwebsite.domain.file.controller.FileUploadSuccessMessage.CREATE_VIDEO_PRESIGNED_URL;

@RestController
@RequestMapping("/api/v1/upload")
@RequiredArgsConstructor
public class FileController implements FileApiSpecification {

    private final S3UploaderService s3UploaderService;

    @PostMapping("/upload-signed-url")
    public SuccessResponse<PresignedUrlResDto> uploadSignedUrl(@RequestBody S3VideoUploadSignedUrlReqDto uploadSignedUrlReqDto) {
        PresignedUrlResDto videoUploadSignedUrl = s3UploaderService.getVideoUploadSignedUrl(uploadSignedUrlReqDto);

        return new SuccessResponse<>(videoUploadSignedUrl, CREATE_VIDEO_PRESIGNED_URL.getMessage());
    }

    @PostMapping("/complete-video-upload")
    public SuccessResponse completeVideoUpload(@RequestBody VideoUploadCompletedDto videoUploadCompletedDto) {
        s3UploaderService.completedUpload(videoUploadCompletedDto);

        return new SuccessResponse(COMPLETE_UPLOAD_VIDEO.getMessage());
    }

}