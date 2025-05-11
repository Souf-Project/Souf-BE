package com.souf.soufwebsite.domain.file.service;

import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3UploaderService s3UploaderService;

    public List<PresignedUrlResDto> generatePresignedUrl(List<String> fileNames){
        List<PresignedUrlResDto> presignedUrlList = fileNames.stream().map(f ->
                        s3UploaderService.generatePresignedUploadUrl(f))
                .collect(Collectors.toList());
        return presignedUrlList;
    }
}
