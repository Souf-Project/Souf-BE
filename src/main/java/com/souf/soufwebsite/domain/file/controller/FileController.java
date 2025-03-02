package com.souf.soufwebsite.domain.file.controller;

import com.souf.soufwebsite.domain.file.dto.FileDto;
import com.souf.soufwebsite.domain.file.entity.FileType;
import com.souf.soufwebsite.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    // 예: form-data로 "file" 필드에 파일 전송, "fileType" 파라미터로 유형 전달
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileDto uploadFile(@RequestPart("file") MultipartFile file,
                              @RequestParam(name = "fileType", required = false, defaultValue = "ETC") FileType fileType)
            throws IOException {

        return fileService.uploadFile(file, fileType);
    }
}