package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatMessageReqDto;
import com.souf.soufwebsite.domain.chat.dto.ChatMessageResDto;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.chat.service.ChatRoomService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.handler.annotation.Payload;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageReqDto request, Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Authentication auth = (Authentication) accessor.getSessionAttributes().get("user");

        if (!(auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetails)) {
            return;
        }

        Member sender = userDetails.getMember();
        ChatRoom room = chatRoomService.getRoomById(request.roomId());

        if (!room.hasParticipant(sender)) {
            throw new AccessDeniedException("채팅방에 참여하지 않은 유저입니다.");
        }

        chatMessageService.saveMessage(room, sender, request.content(), request.type());

        ChatMessageResDto response = new ChatMessageResDto(
                room.getId(),
                sender.getNickname(),
                request.type(),
                request.content(),
                false,
                LocalDateTime.now()
        );

        messagingTemplate.convertAndSend("/topic/chatroom." + room.getId(), response);

    }
}
