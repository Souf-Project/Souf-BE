package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatFileReqDto;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.domain.file.dto.video.VideoDto;
import com.souf.soufwebsite.domain.file.service.FileService;
import com.souf.soufwebsite.global.common.PostType;
import com.souf.soufwebsite.global.success.SuccessResponse;
import jakarta.validation.Valid;
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
public class ChatFileController implements ChatFileApiSpecification{

    private final FileService fileService;
    private final ChatMessageService chatMessageService;

    @PostMapping("/file-upload")
    public ResponseEntity<List<PresignedUrlResDto>> uploadChatFile(@RequestBody ChatFileReqDto reqDto) {
        List<PresignedUrlResDto> url = fileService.generatePresignedUrl("chat", reqDto.originalFileNames());
        return ResponseEntity.ok(url);
    }

    @PostMapping("/upload")
    public SuccessResponse uploadMetadata(@Valid @RequestBody MediaReqDto mediaReqDto){
        chatMessageService.uploadChatFile(mediaReqDto);

        return new SuccessResponse("채팅 파일 업로드에 성공하였습니다.");
    }

    @PostMapping("/video-upload")
    public ResponseEntity<VideoDto> uploadChatVideoFile(@RequestBody ChatFileReqDto reqDto) {
        VideoDto resDto = fileService.configVideoUploadInitiation(reqDto.originalFileNames(), PostType.CHAT);
        return ResponseEntity.ok(resDto);
    }
}
