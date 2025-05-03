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

}