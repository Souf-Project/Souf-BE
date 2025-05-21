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
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessageReqDto request, Principal principal) {
        log.info("sendMessage() 메서드 호출");
        UserDetailsImpl userDetails = (UserDetailsImpl) ((Authentication) principal).getPrincipal();
        Member sender = userDetails.getMember();

        ChatRoom room = chatRoomService.getRoomById(request.roomId());

        log.info("[채팅] 메시지 수신: roomId={}, sender={}, content={}, type={}",
                request.roomId(), sender.getNickname(), request.content(), request.type());

        // 메시지 저장
        chatMessageService.saveMessage(room, sender, request.content(), request.type());

        // 응답 객체 전송
        ChatMessageResDto response = new ChatMessageResDto(
                room.getId(),
                sender.getNickname(),
                request.type(),
                request.content()
        );

        log.info("[채팅] /topic/chatroom/{} 로 메시지 발송 시작", request.roomId());
        messagingTemplate.convertAndSend("/topic/chatroom/" + request.roomId(), response);
        log.info("[채팅] 메시지 발송 완료");
    }
}
