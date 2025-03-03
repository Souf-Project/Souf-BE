package com.souf.soufwebsite.domain.file.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.souf.soufwebsite.domain.file.dto.FileDto;
import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.file.entity.FileType;
import com.souf.soufwebsite.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final AmazonS3 amazonS3;

    private final String bucketName = "iamsouf-bucket";

    public FileDto uploadFile(MultipartFile multipartFile, FileType fileType) throws IOException {
        // 1) 고유한 S3 Key(경로) 생성
        String originalFilename = multipartFile.getOriginalFilename();
        String ext = extractExtension(originalFilename);
        String s3Key = "uploads/" + UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);

        // 2) S3에 파일 업로드
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        amazonS3.putObject(new PutObjectRequest(bucketName, s3Key, multipartFile.getInputStream(), metadata));

        // 3) 업로드한 파일에 대해 프리사인드 URL 생성 (10분 유효)
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10; // 10분 후 만료
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, s3Key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        URL presignedUrl = amazonS3.generatePresignedUrl(urlRequest);
        String fileUrl = presignedUrl.toString();

        // 4) DB에 파일 정보 저장
        File file = File.builder()
                .fileUrl(fileUrl)
                .fileName(originalFilename)
                .fileType(fileType)
                .build();

        File savedFile = fileRepository.save(file);

        // 5) DTO 변환 후 반환
        return FileDto.builder()
                .fileId(savedFile.getId())
                .fileUrl(savedFile.getFileUrl())
                .fileName(savedFile.getFileName())
                .fileType(savedFile.getFileType())
                .build();
    }

    private String extractExtension(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx == -1) return "";
        return filename.substring(idx + 1); // 점 이후 문자열 반환 (확장자)
    }
}