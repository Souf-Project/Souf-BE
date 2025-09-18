package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatMessageResDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomCreateReqDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomResDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.exception.NotChatRoomParticipantException;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.chat.service.ChatRoomService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chatrooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController implements ChatRoomApiSpecificaton {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;

    private Member getCurrentUser() {
        return SecurityUtils.getCurrentMember();
    }

    @PostMapping
    public ResponseEntity<ChatRoomResDto> createChatRoom(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ChatRoomCreateReqDto request
    ) {
        log.info("userDetails: {}", userDetails);

        Member sender = userDetails.getMember();
        Member receiver = memberRepository.findById(request.receiverId())
                .orElseThrow(NotFoundMemberException::new);

        ChatRoom room = chatRoomService.findOrCreateRoom(sender, receiver);

        return ResponseEntity.ok(new ChatRoomResDto(room.getId()));
    }

    @GetMapping
    public ResponseEntity<List<ChatRoomSummaryDto>> getMyChatRooms(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();

        List<ChatRoomSummaryDto> result = chatRoomService.getChatRoomsForUser(member);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResDto>> getMessages(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Member me = userDetails.getMember();
        ChatRoom room = chatRoomService.getRoomById(roomId);

        if (!room.hasParticipant(me)) {
            throw new NotChatRoomParticipantException();
        }

        List<ChatMessage> messages = chatMessageService.getMessages(roomId);
        List<ChatMessageResDto> result = messages.stream()
                .map(msg -> new ChatMessageResDto(
                        roomId,
                        msg.getId(),
                        msg.getSender().getNickname(),
                        msg.getType(),
                        msg.getContent(),
                        msg.isRead(),
                        msg.getCreatedTime()
                ))
                .toList();

        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        Member reader = userDetails.getMember();
        ChatRoom room = chatRoomService.getRoomById(roomId);

        chatMessageService.markMessagesAsRead(room, reader);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{roomId}/exit")
    public ResponseEntity<Void> leaveChatRoom(@PathVariable Long roomId) {
        Member member = getCurrentUser(); // 인증된 사용자
        chatRoomService.exitChatRoom(member, roomId);
        return ResponseEntity.ok().build();
    }
}
