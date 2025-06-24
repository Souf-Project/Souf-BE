package com.souf.soufwebsite.domain.file.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.Media;
import com.souf.soufwebsite.domain.file.entity.MediaType;
import com.souf.soufwebsite.domain.file.entity.PostType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3UploaderService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public PresignedUrlResDto generatePresignedUploadUrl(String prefix, String originalFilename) {
        String ext = extractExtension(originalFilename);
        String fileName = prefix + "/original/" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        GeneratePresignedUrlRequest urlRequest = getGeneratePresignedUrlRequest(fileName);

        URL presignedUrl = amazonS3.generatePresignedUrl(urlRequest);

        return new PresignedUrlResDto(presignedUrl.toString(), fileName);
    }

    private GeneratePresignedUrlRequest getGeneratePresignedUrlRequest(String fileName) {
        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);

        return urlRequest;
    }

    public void uploadFile(MultipartFile multipartFile) throws IOException {
        // 1. S3 Key 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = extractExtension(originalFilename);
        String s3Key = "uploads/" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        // 2. 메타데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        // 3. S3에 업로드
        amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, multipartFile.getInputStream(), metadata));

        PresignedUrlResDto fileUrl = generatePresignedUploadUrl("good", s3Key);

        // 5. File 엔티티 생성 및 저장
        MediaType mediaType = determineFileType(multipartFile);
        if (mediaType == null) {
            throw new IllegalArgumentException("지원하지 않는 파일 확장자입니다.");
        }

        Media media = Media.of(fileUrl.fileUrl(), originalFilename, mediaType, PostType.FEED, 1L);
        //return fileRepository.save(media);
    }

    public void deleteFromS3(String fileUrl) {
        // URL에서 key 추출
        String key = extractKeyFromUrl(fileUrl);
        amazonS3.deleteObject(bucketName, key);
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx == -1) return "";
        return filename.substring(idx + 1); // 점 이후 문자열 반환 (확장자)
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf("uploads/"));
    }

    private MediaType determineFileType(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.contains(".")) return null;

        String ext = filename.substring(filename.lastIndexOf('.') + 1).toUpperCase();

        try {
            return MediaType.valueOf(ext); // enum 상수와 일치하면 반환
        } catch (IllegalArgumentException e) {
            return null; // 또는 FileType.ETC처럼 예외 타입 정의 가능
        }
    }

}
