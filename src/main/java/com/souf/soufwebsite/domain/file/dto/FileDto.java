package com.souf.soufwebsite.domain.file.dto;

import com.souf.soufwebsite.domain.file.entity.FileType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {

    private Long fileId;
    private String fileUrl;   // S3 경로/URL
    private String fileName;   // 원본 파일명
    private FileType fileType; // IMAGE, VIDEO 등
}