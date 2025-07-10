package com.souf.soufwebsite.domain.file.service;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.S3UploadPartsDetailDto;
import com.souf.soufwebsite.domain.file.dto.video.S3VideoUploadSignedUrlReqDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoUploadCompletedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedUploadPartRequest;
import software.amazon.awssdk.services.s3.presigner.model.UploadPartPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3UploaderService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    private static final String videoFolder = "video";

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    // 비디오 업로드 전 세팅 단계
    public VideoResDto initiateUpload(String prefix, String originalFilename) {
        String ext = extractExtension(originalFilename);
        String filename = prefix + "/" + videoFolder + "/" + UUID.randomUUID() + "." + ext;

        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(filename)
                .contentType("video/mp4")
                .build();

        CreateMultipartUploadResponse response = s3Client.createMultipartUpload(createMultipartUploadRequest);

        return new VideoResDto(response.uploadId(), filename);
    }

    public PresignedUrlResDto getVideoUploadSignedUrl(S3VideoUploadSignedUrlReqDto reqDto) {
        UploadPartRequest request = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(reqDto.fileName())
                .uploadId(reqDto.uploadId())
                .partNumber(reqDto.partNumber())
                .build();

        UploadPartPresignRequest uploadPartPresignRequest = UploadPartPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .uploadPartRequest(request)
                .build();

        PresignedUploadPartRequest presignedUploadPartRequest = s3Presigner.presignUploadPart(uploadPartPresignRequest);

        return new PresignedUrlResDto(presignedUploadPartRequest.url().toString(), reqDto.fileName(), "");
    }

    public void completedUpload(VideoUploadCompletedDto dtos) {
        List<CompletedPart> completedPartList = new ArrayList<>();
        for(S3UploadPartsDetailDto dto : dtos.parts()){
             CompletedPart part = CompletedPart.builder()
                    .partNumber(dto.partNumber())
                    .eTag(dto.awsETag())
                    .build();
            completedPartList.add(part);
        }

        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(completedPartList)
                .build();

        String fileName = dtos.fileName();
        CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .uploadId(dtos.uploadId())
                .multipartUpload(completedMultipartUpload)
                .build();

        CompleteMultipartUploadResponse response = s3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    public PresignedUrlResDto generatePresignedUploadUrl(String prefix, String originalFilename) {
        String ext = extractExtension(originalFilename);
        String fileName = prefix + "/original/" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        String mediaType = CONTENT_TYPE_MAP.getOrDefault(ext, "application/octet-stream"); // 기본값 설정

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(mediaType)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(builder -> builder
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
        );

        URL presignedUrl = presignedRequest.url();

        return new PresignedUrlResDto(presignedUrl.toString(), fileName, mediaType);
    }

    public void deleteFromS3(String fileUrl) {
        String key = extractKeyFromUrl(fileUrl);

        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx == -1) return "";
        return filename.substring(idx + 1).toLowerCase();
    }

    private String extractKeyFromUrl(String url) {
        // 예시: https://s3.amazonaws.com/your-bucket/uploads/something.jpg
        int idx = url.indexOf("uploads/");
        if (idx == -1) throw new IllegalArgumentException("Invalid URL format: " + url);
        return url.substring(idx);
    }

    private static final Map<String, String> CONTENT_TYPE_MAP = Map.ofEntries(
            Map.entry("jpg", "image/jpeg"),
            Map.entry("jpeg", "image/jpeg"),
            Map.entry("png", "image/png"),
            Map.entry("webp", "image/webp"),
            Map.entry("pdf", "application/pdf"),
            Map.entry("doc", "application/msword"),
            Map.entry("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            Map.entry("ppt", "application/vnd.ms-powerpoint"),
            Map.entry("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"),
            Map.entry("xls", "application/vnd.ms-excel"),
            Map.entry("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            Map.entry("txt", "text/plain"),
            Map.entry("hwp", "application/vnd.hancom.hwp"),
            Map.entry("zip", "application/zip")
    );
}