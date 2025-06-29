package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatMessageResDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomCreateReqDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomResDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "ChatRoom", description = "채팅방 관련 API")
public interface ChatRoomApiSpecificaton {

    @Operation(summary = "채팅방 생성", description = "두 사용자를 조회해 해당 사용자 간의 채팅방을 생성하고 이미 존재한다면 재사용합니다.")
    @PostMapping
    ResponseEntity<ChatRoomResDto> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ChatRoomCreateReqDto request
    );

    @Operation(summary = "채팅방 조회", description = "해당 사용자가 참여중인 채팅방을 조회합니다.")
    @GetMapping
    ResponseEntity<List<ChatRoomSummaryDto>> getMyChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "메시지 조회", description = "해당 채팅방의 채팅 메시지들을 조회합니다.")
    @GetMapping("/{roomId}/messages")
    ResponseEntity<List<ChatMessageResDto>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "메시지 조회", description = "해당 채팅방의 채팅 메시지들을 읽음 처리합니다.")
    @GetMapping("/{roomId}/messages")
    ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
