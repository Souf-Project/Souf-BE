package com.souf.soufwebsite.domain.file.service;

import com.souf.soufwebsite.domain.file.dto.FileReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.entity.File;
import com.souf.soufwebsite.domain.file.entity.FileType;
import com.souf.soufwebsite.domain.file.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    private final S3UploaderService s3UploaderService;

    public List<PresignedUrlResDto> generatePresignedUrl(List<String> fileNames){
        List<PresignedUrlResDto> presignedUrlList = fileNames.stream().map(f ->
                        s3UploaderService.generatePresignedUploadUrl(f))
                .collect(Collectors.toList());
        return presignedUrlList;
    }

    public List<File> uploadMetadata(FileReqDto files){
        List<File> fileList = new ArrayList<>();
        for(int i=0;i<files.fileUrl().size();i++){
            File file = File.of(files.fileUrl().get(i), files.fileName().get(i), FileType.valueOf(files.fileType().get(i)));
            fileRepository.save(file);
            fileList.add(file);
        }

        return fileList;
    }
}
