package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatFileReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatFileController {

    private final FileService fileService;

    @PostMapping("/file-upload")
    public ResponseEntity<List<PresignedUrlResDto>> uploadChatFile(@RequestBody ChatFileReqDto reqDto) {
        List<PresignedUrlResDto> url = fileService.generatePresignedUrl("chat", reqDto.originalFileNames());
        return ResponseEntity.ok(url);
    }
}
