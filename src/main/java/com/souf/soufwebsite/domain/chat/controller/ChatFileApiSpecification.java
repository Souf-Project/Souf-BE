package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatFileReqDto;
import com.souf.soufwebsite.domain.file.dto.MediaReqDto;
import com.souf.soufwebsite.domain.file.dto.PresignedUrlResDto;
import com.souf.soufwebsite.global.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "ChatFile", description = "채팅 파일 전송 관련 API")
public interface ChatFileApiSpecification {

    @Operation(summary = "채팅 파일 업로드", description = "presignedUrl을 생성하고 반환합니다.")
    @PostMapping
    ResponseEntity<List<PresignedUrlResDto>> uploadChatFile(
            @RequestBody ChatFileReqDto reqDto);

    @Operation(summary = "해당 채팅 관련 파일 정보 저장\", description = \"제공된 presignedUrl을 통해 업로드한 파일의 정보를 DB에도 반영할 수 있도록 서버에게 파일 정보를 보냅니다.")
    @PostMapping
    SuccessResponse uploadMetadata(
            @Valid @RequestBody MediaReqDto mediaReqDto);
}
