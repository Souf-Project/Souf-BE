package com.souf.soufwebsite.domain.chat.controller;

import com.souf.soufwebsite.domain.chat.dto.ChatMessageReqDto;
import com.souf.soufwebsite.domain.chat.dto.ChatMessageResDto;
import com.souf.soufwebsite.domain.chat.entity.ChatRoom;
import com.souf.soufwebsite.domain.chat.service.ChatMessageService;
import com.souf.soufwebsite.domain.chat.service.ChatRoomService;
import com.souf.soufwebsite.domain.member.entity.Member;
import com.souf.soufwebsite.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Controller
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageReqDto request, Principal principal) {
        UserDetailsImpl userDetails = (UserDetailsImpl) ((Authentication) principal).getPrincipal();
        Member sender = userDetails.getMember();

        ChatRoom room = chatRoomService.getRoomById(request.roomId());

        // 메시지 저장
        chatMessageService.saveMessage(room, sender, request.content(), request.type());

        // 응답 객체 전송
        ChatMessageResDto response = new ChatMessageResDto(
                room.getId(),
                sender.getNickname(),
                request.type(),
                request.content()
        );

        messagingTemplate.convertAndSend("/topic/chatroom/" + request.roomId(), response);
    }
}
