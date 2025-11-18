package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatMessageResDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomCreateReqDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomResDto;
import com.souf.soufwebsite.domain.chat.dto.ChatRoomSummaryDto;
import com.souf.soufwebsite.domain.chat.entity.ChatMessage;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.exception.NotChatRoomParticipantException;
import com.souf.soufwebsite.domain.chat.repository.ChatRoomNativeRepository;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.chat.service.ChatRoomService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.domain.member.exception.NotFoundMemberException;
import com.souf.soufwebsite.domain.member.repository.MemberRepository;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import com.souf.soufwebsite.global.success.SuccessResponse;
import com.souf.soufwebsite.global.util.CurrentEmail;
import com.souf.soufwebsite.global.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.souf.soufwebsite.domain.chat.controller.ChatSuccessMessage.COUNT_UNREAD_MESSAGES;

@RestController
@RequestMapping("/api/v1/chatrooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController implements ChatRoomApiSpecification {
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final MemberRepository memberRepository;
    private final ChatRoomNativeRepository chatRoomNativeRepository;

    @Override
    @PostMapping
    public ResponseEntity<ChatRoomResDto> createChatRoom(
            @CurrentEmail String email,
            @RequestBody ChatRoomCreateReqDto request
    ) {
        Member sender = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);
        Member receiver = memberRepository.findById(request.receiverId())
                .orElseThrow(NotFoundMemberException::new);

        ChatRoom room = chatRoomService.findOrCreateRoom(sender, receiver);

        return ResponseEntity.ok(new ChatRoomResDto(room.getId()));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<ChatRoomSummaryDto>> getMyChatRooms(
            @CurrentEmail String email
    ) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);

        List<ChatRoomSummaryDto> result = chatRoomService.getChatRoomsForUser(member);
        return ResponseEntity.ok(result);
    }

    @Override
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<ChatMessageResDto>> getMessages(
            @CurrentEmail String email,
            @PathVariable Long roomId
    ) {
        Member me = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);
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

    @Override
    @PatchMapping("/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @CurrentEmail String email,
            @PathVariable Long roomId
    ) {
        Member reader = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);
        ChatRoom room = chatRoomService.getRoomById(roomId);

        chatMessageService.markMessagesAsRead(room, reader);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/{roomId}/exit")
    public ResponseEntity<Void> leaveChatRoom(
            @CurrentEmail String email,
            @PathVariable Long roomId
    ) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(NotFoundMemberException::new);
        chatRoomService.exitChatRoom(member, roomId);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/unread-count")
    public SuccessResponse<Integer> getUnreadCount(
            @CurrentEmail String email
    ) {
        int count = chatRoomNativeRepository.getTotalUnreadCount(email);
        return new SuccessResponse<>(count, COUNT_UNREAD_MESSAGES.getMessage());
    }
}
